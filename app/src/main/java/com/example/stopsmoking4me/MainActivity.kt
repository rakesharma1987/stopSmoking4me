package com.example.stopsmoking4me

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.stopsmoking4me.adapter.ViewPagerAdapter
import com.example.stopsmoking4me.db.AppDatabase
import com.example.stopsmoking4me.db.DBAdapter
import com.example.stopsmoking4me.factory.AppFactory
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.prefs.MyPreferences
import com.example.stopsmoking4me.receiver.DialogReceiver
import com.example.stopsmoking4me.repository.AppRepository
import com.example.stopsmoking4me.util.Utility
import com.example.stopsmoking4me.viewModel.AppViewModel
import com.example.stopsmoking4me.workManager.MyDialgWorkManager
import com.google.android.material.tabs.TabLayoutMediator
import com.stopsmokingforfamily.aityl.R
import com.stopsmokingforfamily.aityl.databinding.ActivityMainBinding
import com.stopsmokingforfamily.aityl.databinding.LayoutUserProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val tabsArray = arrayOf("Please Consider My Permission", "Quotes / Write Your Quotes", "Reports")
val titlesArray = arrayOf("Mr.", "Mrs.", "Ms.", "Miss")
val relativeArray = arrayOf(
    "God",
    "Mother",
    "Father",
    "Son/s",
    "Daughter/s",
    "Sister/s",
    "Brother/s",
    "Friend/s",
    "Family",
    "Self",
    "Spose",
    "Grandson/s",
    "Granddaughters"
)

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: AppFactory
    lateinit var viewModel: AppViewModel
    private lateinit var msgList: MutableList<Messages>
    private lateinit var quoteList: MutableList<Quotes>
    lateinit var dropDownMessage: MutableList<String>
    var reasonData = Reason()
    private var countYes: Int = 0
    private var countNo: Int = 0
    lateinit var dbAdapter: DBAdapter
//    lateinit var alarmManager: AlarmManager
    lateinit var alarmIntent: PendingIntent
    var twoDaysIinMillis = 0
    private var TAG = "TakeMyPermissionFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val dao = AppDatabase.getInstance(this).dao
        factory = AppFactory(AppRepository(dao))
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        dbAdapter = DBAdapter(this)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = true
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        MyPreferences.isFirstLaunch()

        msgList = mutableListOf()
        msgList.add(Messages(0, "Plan ahead: Have healthy alternatives ready for when craving strikes, such as chewing gum or eating a snack.", "Y"))
        msgList.add(Messages(0, "Yourself occupied: Stay busy with work, hobbies, or social activities to reduce idle time.", "Y"))
        msgList.add(Messages(0, "Practice positive self-talk: Remind yourself of the reasons why you want to quit and stay positive and motivated.", "Y"))
        msgList.add(Messages(0, "Get enough sleep: Lack of sleep can increase stress levels, so make sure to get enough rest each night.", "Y"))
        msgList.add(Messages(0, "Be patient with yourself: Quitting smoking is a process and it takes time, so be kind and understanding with yourself.", "Y"))
        msgList.add(Messages(0, "Exercise: Physical activity can help reduce cravings and manage stress/anxiety.", "Y"))
        msgList.add(Messages(0, "Hobbies: Engage in a new hobby or activity to keep your hands and mind occupied.", "Y"))
        msgList.add(Messages(0, "Relaxation techniques: Try deep breathing, meditation, or yoga to manage stress and reduce cravings.", "Y"))
        msgList.add(Messages(0, "Healthy eating: Maintain A healthy diet and snack on healthy foods when cravings strike.", "Y"))
        msgList.add(Messages(0, "Therapeutic approaches: Consider seeking the help of a therapist or counselor to support your quitting journey.", "Y"))
        msgList.add(Messages(0, "Social activities: Spend time with friends and family and participate in social activities.", "Y"))
        msgList.add(Messages(0, "Distraction techniques: Distract yourself with a book, movie, music, or puzzle to help manage cravings.", "Y"))
        msgList.add(Messages(0, "Healthy eating: Maintain a healthy diet and snack on healthy foods when cravings strike.", "Y"))
        msgList.add(Messages(0, "Be Patient and Persistent: Quitting smoking is a process, and it's normal to experience setbacks. If you slip up, don't be too hard on yourself. Learn from the experience and recommit to your decision.", "Y"))
        msgList.add(Messages(0, "Create a Smoke-Free Environment: Avoid places where people smoke, at least during the initial stages of quitting.", "Y"))
        msgList.add(Messages(0, "Get Rid of Smoking Paraphernalia: Discard all cigarettes, lighters, and ashtrays from your home, car, and workplace. Removing these reminders can make it easier to resist the temptation.", "Y"))
        msgList.add(Messages(0, "Understand Your Triggers: Identify the situations, emotions, or habits that trigger your urge to smoke. Once you know them, find healthier alternatives or coping mechanisms for those triggers.", "Y"))
        msgList.add(Messages(0, "Regular practice of meditation: Regular meditation enhances attention and concentration, leading to improved productivity and cognitive abilities.", "Y"))

        msgList.add(Messages(0, "Sleep: Make sure you're getting enough sleep, as lack of sleep can increase stress levels.", "R"))
        msgList.add(Messages(0, "Be Patient and Persistent: Quitting smoking is a process, and it's normal to experience setbacks. If you slip up, don't be too hard on yourself. Learn from the experience and recommit to your decision.", "R"))
        msgList.add(Messages(0, "Create a Smoke-Free Environment: Avoid places where people smoke, at least during the initial stages of quitting.", "R"))
        msgList.add(Messages(0, "Get Rid of Smoking Paraphernalia: Discard all cigarettes, lighters, and ashtrays from your home, car, and workplace. Removing these reminders can make it easier to resist the temptation.", "R"))
        msgList.add(Messages(0, "Mindfulness: Focus on the present moment and be mindful of your thoughts and feelings to help reduce cravings.", "R"))
        msgList.add(Messages(0, "Identify and avoid triggers that lead to smoking.", "R"))
        msgList.add(Messages(0, "Keep yourself occupied, engage in physical activity or hobbies.", "R"))
        msgList.add(Messages(0, "Seek support from family, trusted friends, or a support group.", "R"))
        msgList.add(Messages(0, "Meditation can promote emotional balance, reduce symptoms of anxiety and depression, and increase overall feelings of happiness and well-being.", "R"))
        msgList.add(Messages(0, "Reward yourself for milestones achieved in quitting smoking.", "R"))
        msgList.add(Messages(0, "Affirmations: Use positive affirmations related to quitting smoking. Repeat statements like 'I am free from smoking', 'I am in control of my choices', or 'I am committed to my health and well-being', Affirmations can reinforce your determination to quit smoking.", "R"))
        msgList.add(Messages(0, "Regular meditation can help build resilience and the ability to bounce back from challenging situations.", "R"))
        msgList.add(Messages(0, "Stay positive and focus on the benefits of quitting, such as improved health and increased energy.", "R"))
        msgList.add(Messages(0, "Avoid Alcohol and Coffee: These beverages can be associated with smoking, so it might be helpful to cut down on them, especially during the initial quitting phase.", "R"))
        msgList.add(Messages(0, "Focus on the benefits: Remind yourself of the positive effects quitting smoking will have on your health and wellbeing.", "R"))
        msgList.add(Messages(0, "Find a replacement activity: Try to find a new hobby or physical activity to keep yourself occupied and distracted.", "R"))
        msgList.add(Messages(0, "Understand Your Triggers: Identify the situations, emotions, or habits that trigger your urge to smoke. Once you know them, find healthier alternatives or coping mechanisms for those triggers.", "R"))

        msgList.add(Messages(0, "Focus on the benefits: Remind yourself of the positive effects quitting smoking will have on your health and wellbeing", "G"))
        msgList.add(Messages(0, "Create a Smoke-Free Environment: Avoid places where people smoke, at least during the initial stages of quitting.", "G"))
        msgList.add(Messages(0, "Find a replacement activity: Try to find a new hobby or physical activity to keep yourself occupied and distracted.", "G"))
        msgList.add(Messages(0, "Keep a journal: Write down your thoughts and feelings to track your progress and stay motivated.", "G"))
        msgList.add(Messages(0, "Celebrate milestones: Reward yourself for reaching certain milestones, such as going a week without smoking.", "G"))
        msgList.add(Messages(0, "Be kind to yourself: Quitting smoking is a challenging process, and it's important to be patient and understanding with yourself.", "G"))
        msgList.add(Messages(0, "Visualize success: Imagine yourself as a non-smoker and the positive changes quitting will bring to your life.", "G"))
        msgList.add(Messages(0, "Avoid temptations: Steer clear of situations that might trigger the urge to smoke.", "G"))
        msgList.add(Messages(0, "Stay active: Physical activities can help reduce cravings and boost your mood.", "G"))
        msgList.add(Messages(0, "Meditation can enhance creative thinking and problem-solving abilities by quieting the mind and promoting new perspectives.", "G"))
        msgList.add(Messages(0, "Be Patient and Persistent: Quitting smoking is a process, and it's normal to experience setbacks. If you slip up, don't be too hard on yourself. Learn from the experience and recommit to your decision.", "G"))
        msgList.add(Messages(0, "Don't give up: Quitting smoking is a process and it takes time, but with determination and support, you can be successful.", "G"))
        msgList.add(Messages(0, "Practice stress-management techniques: Try relaxation techniques such as deep breathing, meditation, or yoga to manage stress.", "G"))
        msgList.add(Messages(0, "Find a healthy outlet: Engage in physical activity or hobbies to channel stress in a positive way.", "G"))
        msgList.add(Messages(0, "Avoid triggers: Identify and avoid situations that trigger the urge to smoke, such as alcohol or certain people.", "G"))
        msgList.add(Messages(0, "Understand Your Triggers: Identify the situations, emotions, or habits that trigger your urge to smoke. Once you know them, find healthier alternatives or coping mechanisms for those triggers.", "G"))
        msgList.add(Messages(0, "Get Rid of Smoking Paraphernalia: Discard all cigarettes, lighters, and ashtrays from your home, car, and workplace. Removing these reminders can make it easier to resist the temptation.", "G"))
        msgList.add(Messages(0, "Through meditation, you can develop a deeper understanding of your thoughts, emotions, and behaviors, leading to greater self-awareness and self-acceptance.", "G"))

        quoteList = mutableListOf()
        quoteList.add(
            Quotes(
                0,
                "Every time you try to quit smoking you are actually getting closer to staying smoke-free.",
                "Henry Ford"
            )
        )
        quoteList.add(
            Quotes(
                0,
                "A cigarette is the only consumer product which when used as directed kills its consumer.",
                "Gro Brundtland"
            )
        )
        quoteList.add(
            Quotes(
                0,
                "You're always better off if you quit smoking; it's never too late.",
                "Loni Anderson"
            )
        )
        quoteList.add(
            Quotes(
                0,
                "Today just might be the best day to start seriously thinking about quitting smoking.",
                "Alexander Woollcott"
            )
        )
        quoteList.add(
            Quotes(
                0,
                "Giving up smoking is the easiest thing in the world. I know because I've done it thousand times.",
                "Mark Twain"
            )
        )
        quoteList.add(
            Quotes(
                0,
                "Quitting smoking might be the hardest thing to do in life, but at least you will have one.",
                "Anonymous"
            )
        )

        dropDownMessage = mutableListOf()
        dropDownMessage.add("Please Select Option")
        dropDownMessage.add("Stress")
        dropDownMessage.add("Anxiety")
        dropDownMessage.add("Depression")
        dropDownMessage.add("Relaxation")
        dropDownMessage.add("Relieve boredom")
        dropDownMessage.add("Happiness")
        dropDownMessage.add("Before a meal")
        dropDownMessage.add("After a meal")
        dropDownMessage.add("During meal")
        dropDownMessage.add("Break at work")
        dropDownMessage.add("Trying to quit but triggering more")
        dropDownMessage.add("Sights, smells, and places")
        dropDownMessage.add("Restaurent and Bar")
        dropDownMessage.add("Around other smokers")
        dropDownMessage.add("Specifi Time")
        dropDownMessage.add("Other")

//        if (dbAdapter.get30DaysAnalytics().isEmpty()){
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (2, '2023-08-07', 11, 'Monday', 'After Meals', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (225, '2023-08-23', 16, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (230, '2023-08-23', 21, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (234, '2023-08-23', 1, 'Thursday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (235, '2023-08-23', 2, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (236, '2023-08-23', 3, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (237, '2023-08-23', 4, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (238, '2023-08-23', 5, 'Thursday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (242, '2023-08-23', 15, 'Thursday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (243, '2023-08-23', 16, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (244, '2023-08-23', 17, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (245, '2023-08-23', 18, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (246, '2023-08-23', 19, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (247, '2023-08-23', 20, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (248, '2023-08-23', 21, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (249, '2023-08-23', 22, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (250, '2023-08-23', 23, 'Thursday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (251, '2023-08-23', 24, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (252, '2023-08-23', 1, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (253, '2023-08-23', 2, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (254, '2023-08-23', 3, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (255, '2023-08-23', 4, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (256, '2023-08-23', 5, 'Thursday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (257, '2023-08-23', 6, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (258, '2023-08-23', 7, 'Thursday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (266, '2023-08-22', 1, 'Wednesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (270, '2023-08-22', 5, 'Wednesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (271, '2023-08-22', 6, 'Wednesday', 'Depression', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (273, '2023-08-22', 8, 'Wednesday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (277, '2023-08-22', 12, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (280, '2023-08-22', 19, 'Wednesday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (281, '2023-08-22', 20, 'Wednesday', 'Relaxation ', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (282, '2023-08-22', 21, 'Wednesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (283, '2023-08-22', 22, 'Wednesday', 'Trying to quit but triggering more', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (284, '2023-08-22', 23, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (285, '2023-08-22', 24, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (286, '2023-08-22', 1, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (287, '2023-08-22', 2, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (288, '2023-08-22', 3, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (289, '2023-08-22', 4, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (290, '2023-08-22', 5, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (291, '2023-08-22', 6, 'Wednesday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (292, '2023-08-22', 7, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (293, '2023-08-22', 8, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (294, '2023-08-22', 9, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (295, '2023-08-22', 10, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (296, '2023-08-22', 11, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (299, '2023-08-22', 14, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (300, '2023-08-21', 14, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (302, '2023-08-21', 16, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (306, '2023-08-21', 20, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (307, '2023-08-21', 21, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (308, '2023-08-21', 22, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (311, '2023-08-21', 1, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (313, '2023-08-21', 3, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (314, '2023-08-21', 4, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (315, '2023-08-21', 5, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (324, '2023-08-21', 14, 'Tuesday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (330, '2023-08-21', 15, 'Tuesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (331, '2023-08-21', 16, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (332, '2023-08-21', 22, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (333, '2023-08-21', 23, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (334, '2023-08-21', 24, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (336, '2023-08-21', 2, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (337, '2023-08-21', 3, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (338, '2023-08-21', 4, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (339, '2023-08-21', 5, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (340, '2023-08-21', 6, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (341, '2023-08-21', 7, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (342, '2023-08-21', 8, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (343, '2023-08-21', 9, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (345, '2023-08-21', 11, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (346, '2023-08-21', 12, 'Tuesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (347, '2023-08-21', 13, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (348, '2023-08-21', 14, 'Tuesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (349, '2023-08-21', 15, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (351, '2023-08-21', 17, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (360, '2023-08-20', 3, 'Monday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (365, '2023-08-20', 8, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (367, '2023-08-20', 10, 'Monday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (372, '2023-08-20', 21, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (373, '2023-08-20', 22, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (374, '2023-08-20', 23, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (375, '2023-08-20', 24, 'Monday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (376, '2023-08-20', 1, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (377, '2023-08-20', 2, 'Monday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (378, '2023-08-20', 3, 'Monday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (379, '2023-08-20', 4, 'Monday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (381, '2023-08-20', 6, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (382, '2023-08-20', 7, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (383, '2023-08-20', 8, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (384, '2023-08-20', 9, 'Monday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (385, '2023-08-20', 10, 'Monday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (386, '2023-08-20', 11, 'Monday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (387, '2023-08-20', 12, 'Monday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (392, '2023-08-19', 7, 'Sunday', 'Around other smokers', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (399, '2023-08-19', 14, 'Sunday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (403, '2023-08-19', 18, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (405, '2023-08-19', 4, 'Sunday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (406, '2023-08-19', 5, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (407, '2023-08-19', 6, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (408, '2023-08-19', 7, 'Sunday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (409, '2023-08-19', 8, 'Sunday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (410, '2023-08-19', 9, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (411, '2023-08-19', 10, 'Sunday', 'Relaxation ', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (412, '2023-08-19', 11, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (413, '2023-08-19', 12, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (414, '2023-08-19', 13, 'Sunday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (416, '2023-08-19', 15, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (417, '2023-08-19', 16, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (418, '2023-08-19', 17, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (419, '2023-08-19', 18, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (420, '2023-08-19', 19, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (422, '2023-08-18', 10, 'Saturday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (427, '2023-08-18', 15, 'Saturday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (436, '2023-08-18', 24, 'Saturday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (437, '2023-08-18', 1, 'Saturday', 'Around other smokers', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (439, '2023-08-18', 3, 'Saturday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (441, '2023-08-18', 10, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (442, '2023-08-18', 11, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (443, '2023-08-18', 12, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (444, '2023-08-18', 13, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (445, '2023-08-18', 14, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (446, '2023-08-18', 15, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (447, '2023-08-18', 16, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (448, '2023-08-18', 17, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (449, '2023-08-18', 18, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (450, '2023-08-18', 19, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (451, '2023-08-18', 20, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (452, '2023-08-18', 21, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (455, '2023-08-18', 24, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (456, '2023-08-18', 1, 'Saturday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (457, '2023-08-18', 2, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (458, '2023-08-18', 3, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (461, '2023-08-17', 17, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (463, '2023-08-17', 19, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (464, '2023-08-17', 20, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (466, '2023-08-17', 22, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (468, '2023-08-17', 24, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (469, '2023-08-17', 1, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (472, '2023-08-17', 4, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (477, '2023-08-17', 15, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (478, '2023-08-17', 16, 'Friday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (479, '2023-08-17', 17, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (481, '2023-08-17', 19, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (482, '2023-08-17', 20, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (485, '2023-08-17', 23, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (486, '2023-08-17', 24, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (487, '2023-08-17', 1, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (488, '2023-08-17', 2, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (489, '2023-08-17', 3, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (490, '2023-08-17', 4, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (491, '2023-08-17', 5, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (493, '2023-08-17', 7, 'Friday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (494, '2023-08-17', 8, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (495, '2023-08-16', 5, 'Thursday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (499, '2023-08-16', 9, 'Thursday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (501, '2023-08-16', 11, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (505, '2023-08-16', 5, 'Thursday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (506, '2023-08-16', 6, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (508, '2023-08-16', 8, 'Thursday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (509, '2023-08-16', 9, 'Thursday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (510, '2023-08-16', 10, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (511, '2023-08-16', 11, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (512, '2023-08-16', 12, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (513, '2023-08-16', 13, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (514, '2023-08-16', 14, 'Thursday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (521, '2023-08-15', 21, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (522, '2023-08-15', 22, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (525, '2023-08-15', 1, 'Wednesday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (526, '2023-08-15', 2, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (528, '2023-08-15', 4, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (529, '2023-08-15', 15, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (530, '2023-08-15', 16, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (531, '2023-08-15', 17, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (533, '2023-08-15', 19, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (534, '2023-08-15', 20, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (535, '2023-08-15', 21, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (538, '2023-08-15', 24, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (539, '2023-08-15', 1, 'Wednesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (540, '2023-08-15', 2, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (541, '2023-08-15', 3, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (542, '2023-08-15', 4, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (545, '2023-08-14', 10, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (548, '2023-08-14', 13, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (550, '2023-08-14', 14, 'Tuesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (554, '2023-08-13', 5, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (556, '2023-08-13', 7, 'Monday', 'Depression', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (557, '2023-08-13', 2, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (558, '2023-08-13', 3, 'Monday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (561, '2023-08-12', 14, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (563, '2023-08-12', 16, 'Sunday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (564, '2023-08-12', 17, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (569, '2023-08-12', 22, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (572, '2023-08-12', 1, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (573, '2023-08-12', 12, 'Sunday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (574, '2023-08-12', 13, 'Sunday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (575, '2023-08-12', 14, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (576, '2023-08-12', 15, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (577, '2023-08-12', 16, 'Sunday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (578, '2023-08-12', 17, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (579, '2023-08-12', 18, 'Sunday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (580, '2023-08-12', 19, 'Sunday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (581, '2023-08-12', 20, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (582, '2023-08-12', 21, 'Sunday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (583, '2023-08-12', 22, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (584, '2023-08-12', 23, 'Sunday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (585, '2023-08-12', 24, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (586, '2023-08-12', 1, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (596, '2023-08-11', 3, 'Saturday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (597, '2023-08-11', 4, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (598, '2023-08-11', 5, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (600, '2023-08-11', 7, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (602, '2023-08-11', 9, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (603, '2023-08-11', 10, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (604, '2023-08-11', 11, 'Saturday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (605, '2023-08-10', 14, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (606, '2023-08-10', 15, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (609, '2023-08-10', 18, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (610, '2023-08-10', 19, 'Friday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (616, '2023-08-10', 1, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (618, '2023-08-10', 14, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (619, '2023-08-10', 15, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (620, '2023-08-10', 16, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (621, '2023-08-10', 17, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (622, '2023-08-10', 18, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (623, '2023-08-10', 19, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (624, '2023-08-10', 20, 'Friday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (626, '2023-08-10', 22, 'Friday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (627, '2023-08-10', 23, 'Friday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (628, '2023-08-10', 24, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (629, '2023-08-10', 1, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (630, '2023-08-10', 2, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (632, '2023-08-09', 5, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (635, '2023-08-09', 8, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (639, '2023-08-09', 12, 'Thursday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (641, '2023-08-09', 6, 'Thursday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (642, '2023-08-09', 7, 'Thursday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (644, '2023-08-09', 9, 'Thursday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (645, '2023-08-09', 10, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (646, '2023-08-09', 11, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (648, '2023-08-09', 13, 'Thursday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (650, '2023-08-08', 11, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (651, '2023-08-08', 12, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (655, '2023-08-08', 16, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (657, '2023-08-08', 18, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (660, '2023-08-08', 21, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (663, '2023-08-08', 24, 'Wednesday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (665, '2023-08-08', 2, 'Wednesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (667, '2023-08-08', 10, 'Wednesday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (668, '2023-08-08', 11, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (669, '2023-08-08', 12, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (670, '2023-08-08', 13, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (673, '2023-08-08', 16, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (674, '2023-08-08', 17, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (675, '2023-08-08', 18, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (676, '2023-08-08', 19, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (677, '2023-08-08', 20, 'Wednesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (678, '2023-08-08', 21, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (687, '2023-08-07', 23, 'Tuesday', 'After Meals', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (689, '2023-08-07', 1, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (695, '2023-08-07', 7, 'Tuesday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (696, '2023-08-07', 8, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (697, '2023-08-07', 9, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (698, '2023-08-07', 15, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (699, '2023-08-07', 16, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (700, '2023-08-07', 17, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (701, '2023-08-07', 18, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (702, '2023-08-07', 19, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (703, '2023-08-07', 20, 'Tuesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (705, '2023-08-07', 4, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (706, '2023-08-07', 5, 'Tuesday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (707, '2023-08-07', 6, 'Tuesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (709, '2023-08-07', 8, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (711, '2023-08-06', 6, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (713, '2023-08-06', 8, 'Monday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (714, '2023-08-06', 9, 'Monday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (716, '2023-08-06', 11, 'Monday', 'Around other smokers', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (717, '2023-08-06', 12, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (720, '2023-08-06', 6, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (721, '2023-08-06', 7, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (722, '2023-08-06', 8, 'Monday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (723, '2023-08-06', 9, 'Monday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (724, '2023-08-06', 10, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (725, '2023-08-06', 11, 'Monday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (729, '2023-08-05', 17, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (730, '2023-08-05', 18, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (732, '2023-08-05', 20, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (735, '2023-08-05', 23, 'Sunday', 'Relaxation ', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (741, '2023-08-05', 5, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (742, '2023-08-05', 17, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (743, '2023-08-05', 18, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (744, '2023-08-05', 19, 'Sunday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (745, '2023-08-05', 20, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (746, '2023-08-05', 21, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (747, '2023-08-05', 22, 'Sunday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (748, '2023-08-05', 23, 'Sunday', 'Relaxation ', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (751, '2023-08-05', 2, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (752, '2023-08-05', 3, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (753, '2023-08-05', 4, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (754, '2023-08-05', 5, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (757, '2023-08-04', 11, 'Saturday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (760, '2023-08-04', 14, 'Saturday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (763, '2023-08-04', 9, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (765, '2023-08-04', 11, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (766, '2023-08-04', 12, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (767, '2023-08-04', 13, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (768, '2023-08-04', 14, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (769, '2023-08-04', 15, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (770, '2023-08-04', 16, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (776, '2023-08-03', 4, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (777, '2023-08-03', 5, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (778, '2023-08-03', 6, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (779, '2023-08-03', 7, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (780, '2023-08-03', 8, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (781, '2023-08-02', 21, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (782, '2023-08-02', 22, 'Thursday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (783, '2023-08-02', 23, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (786, '2023-08-02', 2, 'Thursday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (787, '2023-08-02', 3, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (788, '2023-08-02', 21, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (789, '2023-08-02', 22, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (790, '2023-08-02', 23, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (792, '2023-08-02', 1, 'Thursday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (793, '2023-08-02', 2, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (794, '2023-08-02', 3, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (795, '2023-08-01', 11, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (796, '2023-08-01', 12, 'Wednesday', 'Trying to quit but triggering more', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (798, '2023-08-01', 14, 'Wednesday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (800, '2023-08-01', 16, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (801, '2023-08-01', 17, 'Wednesday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (804, '2023-08-01', 20, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (805, '2023-08-01', 11, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (806, '2023-08-01', 12, 'Wednesday', 'Trying to quit but triggering more', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (807, '2023-08-01', 13, 'Wednesday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (808, '2023-08-01', 14, 'Wednesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (811, '2023-07-31', 21, 'Tuesday', 'Depression', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (812, '2023-07-31', 22, 'Tuesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (821, '2023-07-31', 7, 'Tuesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (825, '2023-07-31', 19, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (826, '2023-07-31', 20, 'Tuesday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (828, '2023-07-31', 22, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (829, '2023-07-31', 23, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (830, '2023-07-31', 24, 'Tuesday', 'Before Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (831, '2023-07-31', 1, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (832, '2023-07-31', 2, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (833, '2023-07-31', 3, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (834, '2023-07-31', 4, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (835, '2023-07-31', 5, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (836, '2023-07-31', 6, 'Tuesday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (837, '2023-07-31', 7, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (838, '2023-07-31', 8, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (839, '2023-07-31', 9, 'Tuesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (840, '2023-07-31', 10, 'Tuesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (841, '2023-07-30', 13, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (846, '2023-07-30', 18, 'Monday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (847, '2023-07-30', 13, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (848, '2023-07-30', 14, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (849, '2023-07-30', 15, 'Monday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (850, '2023-07-30', 16, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (851, '2023-07-30', 17, 'Monday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (852, '2023-07-30', 18, 'Monday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (853, '2023-07-29', 5, 'Sunday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (857, '2023-07-29', 9, 'Sunday', 'Trying to quit but triggering more', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (859, '2023-07-29', 11, 'Sunday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (861, '2023-07-29', 5, 'Sunday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (862, '2023-07-29', 6, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (863, '2023-07-29', 7, 'Sunday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (864, '2023-07-29', 8, 'Sunday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (865, '2023-07-29', 9, 'Sunday', 'Trying to quit but triggering more', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (866, '2023-07-29', 10, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (867, '2023-07-29', 11, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (868, '2023-07-29', 12, 'Sunday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (875, '2023-07-28', 15, 'Saturday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (878, '2023-07-28', 18, 'Saturday', 'During meal', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (884, '2023-07-28', 24, 'Saturday', 'After Meals', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (889, '2023-07-28', 12, 'Saturday', 'Trying to quit but triggering more', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (890, '2023-07-28', 13, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (891, '2023-07-28', 14, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (893, '2023-07-28', 16, 'Saturday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (894, '2023-07-28', 17, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (895, '2023-07-28', 18, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (896, '2023-07-28', 19, 'Saturday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (897, '2023-07-28', 20, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (898, '2023-07-28', 21, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (899, '2023-07-28', 22, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (900, '2023-07-28', 23, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (901, '2023-07-28', 24, 'Saturday', 'After Meals', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (902, '2023-07-28', 1, 'Saturday', 'During meal', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (904, '2023-07-28', 3, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (905, '2023-07-28', 4, 'Saturday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (910, '2023-07-27', 19, 'Friday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (912, '2023-07-27', 21, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (915, '2023-07-27', 24, 'Friday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (916, '2023-07-27', 1, 'Friday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (919, '2023-07-27', 4, 'Friday', 'Before Meals', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (920, '2023-07-27', 5, 'Friday', 'Depression', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (924, '2023-07-27', 15, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (925, '2023-07-27', 16, 'Friday', 'Around other smokers', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (926, '2023-07-27', 17, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (927, '2023-07-27', 18, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (928, '2023-07-27', 19, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (930, '2023-07-27', 21, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (931, '2023-07-27', 22, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (932, '2023-07-27', 23, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (933, '2023-07-27', 24, 'Friday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (934, '2023-07-27', 1, 'Friday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (938, '2023-07-26', 1, 'Thursday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (940, '2023-07-26', 3, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (942, '2023-07-26', 5, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (943, '2023-07-26', 6, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (946, '2023-07-26', 9, 'Thursday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (949, '2023-07-26', 12, 'Thursday', 'Depression', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (952, '2023-07-26', 22, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (953, '2023-07-26', 23, 'Thursday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (954, '2023-07-26', 24, 'Thursday', 'Relaxation ', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (955, '2023-07-26', 1, 'Thursday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (956, '2023-07-26', 2, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (957, '2023-07-26', 3, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (958, '2023-07-26', 12, 'Thursday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (959, '2023-07-26', 13, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (960, '2023-07-26', 14, 'Thursday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (965, '2023-07-25', 15, 'Wednesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (966, '2023-07-25', 16, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (971, '2023-07-25', 21, 'Wednesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (972, '2023-07-25', 11, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (974, '2023-07-25', 13, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (975, '2023-07-25', 14, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (976, '2023-07-25', 15, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (977, '2023-07-25', 16, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (978, '2023-07-25', 17, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (979, '2023-07-25', 18, 'Wednesday', 'Happiness', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (980, '2023-07-25', 19, 'Wednesday', 'Break at work', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (981, '2023-07-25', 20, 'Wednesday', 'Depression', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (982, '2023-07-25', 21, 'Wednesday', 'Stress', 'No')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (984, '2023-07-24', 15, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (987, '2023-07-24', 18, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (988, '2023-07-24', 19, 'Tuesday', 'Break at work', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (990, '2023-07-24', 21, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (993, '2023-07-24', 24, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (994, '2023-07-24', 24, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (996, '2023-07-24', 2, 'Tuesday', 'Happiness', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (997, '2023-07-24', 3, 'Tuesday', 'Stress', 'Yes')")
//            dbAdapter.sqliteDatabase.execSQL("INSERT INTO TabQuitSmokingApp (SrNo, Date, Hour, Day, Reason, Smoking) VALUES (999, '2023-07-24', 5, 'Tuesday', 'Stress', 'Yes')")
//
//
//        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            if (viewModel.isEmpty()) {
                viewModel.insertMessages(msgList = msgList)
            }

            if (viewModel.isEmptyQuotTable()) {
                viewModel.insertQuotes(quotes = quoteList)
            }
        }

        viewModel.getYesCount().observe(this@MainActivity, Observer {
            countYes = it
            Log.d("COUNT", "COUNT: $countYes")
        })

        viewModel.getNoCount().observe(this@MainActivity, Observer {
            countNo = it
            Log.d("COUNT", "COUNT: $countNo")
        })

        viewModel.getTotalCount().observe(this@MainActivity, Observer {
            Log.d("Reason: ", "onResume: $it")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_edit -> {
                var dialog = Dialog(this@MainActivity)
                dialog.setCancelable(false)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                var lupBinding: LayoutUserProfileBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(dialog.context), R.layout.layout_user_profile, null, false
                )
                dialog.setContentView(lupBinding.root)
                dialog.show()
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                //Setting data from preference
                if (MyPreferences.getTitle()!!.isNotEmpty()) lupBinding.titleDropdown.setText(
                    MyPreferences.getTitle()
                )
                if (MyPreferences.getTitleName()!!.isNotEmpty()) lupBinding.name.setText(
                    MyPreferences.getTitleName()
                )
                if (MyPreferences.getWhom()!!.isNotEmpty()) lupBinding.dropdownWhom.setText(
                    MyPreferences.getWhom()
                )
                if (MyPreferences.getWhomName()!!.isNotEmpty()) lupBinding.whomName.setText(
                    MyPreferences.getWhomName()
                )

                var titleAdapter = ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    titlesArray
                )
                var relativeAdapter = ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    relativeArray
                )
                lupBinding.dropdownWhom.setAdapter(relativeAdapter)
                lupBinding.titleDropdown.setAdapter(titleAdapter)

                lupBinding.tilDropdown.setOnClickListener {
                    Utility().hideSoftKeyBoard(this, lupBinding.tilDropdown)
                }

                lupBinding.titleDropdown.setOnItemClickListener { parent, view, position, id ->
//                    view.hideKeyboard()
                    Utility().hideSoftKeyBoard(this, lupBinding.titleDropdown)
                    reasonData.title = titleAdapter.getItem(position).toString()
                    MyPreferences.saveTitle(reasonData.title)
                }

                lupBinding.tilForWhom.setOnClickListener {
                    Utility().hideSoftKeyBoard(this, lupBinding.tilForWhom)
                }

                lupBinding.dropdownWhom.setOnItemClickListener { parent, view, position, id ->
//                    view.hideKeyboard()
                    Utility().hideSoftKeyBoard(this, lupBinding.dropdownWhom)
                    reasonData.forWhom = relativeAdapter.getItem(position).toString()
                    MyPreferences.saveWhom(reasonData.forWhom)
                }

                lupBinding.ivClose.setOnClickListener {
                    dialog.dismiss()
                }
                lupBinding.btnSubmit.setOnClickListener {
                    if (lupBinding.titleDropdown.text.isNotEmpty() || lupBinding.tilName.editText!!.text.isNotEmpty() ||
                        lupBinding.titleDropdown.text.isNotEmpty() || lupBinding.tilForWhom.editText!!.text.isNotEmpty()
                    ) {
                        reasonData.name = lupBinding.tilName.editText!!.text.toString()
                        reasonData.whomName = lupBinding.etWhomName.editText!!.text.toString()
                        MyPreferences.saveTitleName(reasonData.name)
                        MyPreferences.saveWhomName(reasonData.whomName)
                        dialog.dismiss()

                    } else {
                        showToast("Please fill all fields.")
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getSystemDate(): String {
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return simpleDateFormat.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity onDestroy: ")
    }

    fun startPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val showDialogInPeriodicRequest = PeriodicWorkRequest.Builder(
            MyDialgWorkManager::class.java, 16, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(showDialogInPeriodicRequest)
//        workManager.enqueueUniquePeriodicWork(
//            "dialogWork",
//            ExistingPeriodicWorkPolicy.KEEP,
//            showDialogInPeriodicRequest
//        )
//
//        workManager.getWorkInfoByIdLiveData(showDialogInPeriodicRequest.id)
//            .observe(this, Observer {
//                if (it != null && it.state == WorkInfo.State.SUCCEEDED){
//                    Log.d("PERIODIC_WORK", "onResume: ${it.state.name}")
//                    val dialogFragment = MyDialogFragment()
//                    dialogFragment.show(supportFragmentManager, "dialog")
//                }
//            })
    }

    fun hasTwoDaysPassed(): Boolean {
//        var firstLaunchTime: Long = MyPreferences.firstTimeLaunch
//        var currentTime: Long = System.currentTimeMillis()
//        var timeDifference: Long = currentTime - firstLaunchTime
//        twoDaysIinMillis = 259200000
//        return timeDifference > twoDaysIinMillis
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - MyPreferences.installationDate
        val daysDifference = difference / (24 * 60 * 60 * 1000)
        return daysDifference >= 3
//        return difference >= 5000
    }

    fun setRepeatingAlarm(context: Context) {
        var intent = Intent(this, DialogReceiver::class.java)
        var pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + twoDaysIinMillis,
            pendingIntent
        )
    }

    private fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}