package com.example.stopsmoking4me

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stopsmoking4me.adapter.ViewPagerAdapter
import com.example.stopsmoking4me.databinding.ActivityMainBinding
import com.example.stopsmoking4me.databinding.LayoutUserProfileBinding
import com.example.stopsmoking4me.db.AppDatabase
import com.example.stopsmoking4me.db.DBAdapter
import com.example.stopsmoking4me.factory.AppFactory
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.repository.AppRepository
import com.example.stopsmoking4me.viewModel.AppViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val tabsArray = arrayOf("Take My \n Permission", "Quotes", "States \u0026 Charts")
val titlesArray = arrayOf("Mr.", "Mrs.", "Ms.", "Miss")
val relativeArray = arrayOf("Mother", "Father", "Son/s", "Daughter/s", "Sister/s", "Brother/s", "Friend/s", "Family", "Self", "Spose",
                    "Grandson/s", "Granddaughters")
class MainActivity : AppCompatActivity() {
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
//    var reasonList = ArrayList<Reason>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        val dao = AppDatabase.getInstance(this).dao
        factory = AppFactory(AppRepository(dao))
        viewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        dbAdapter = DBAdapter(this)
        for (i in 1..50){
            if (i % 5 == 0){
                dbAdapter.saveData("", "No")
            }else{
                dbAdapter.saveData("Break at work", "Yes")
            }
        }

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = true
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        msgList = mutableListOf()
        msgList.add(Messages(0, "Plan ahead: have healthy alternatives ready for when stress strikes, such as chewing gum or eating a snack.", "Y"))
        msgList.add(Messages(0, "yourself occupied: stay busy with work, hobbies, or social activities to reduce idle time.", "Y"))
        msgList.add(Messages(0, "Practice positive self-talk: remind yourself of the reasons why you want to quit and stay positive and motivated.", "Y"))
        msgList.add(Messages(0, "Use nicotine replacement therapy: consider using nicotine replacement therapy, such as patches or gum, to manage cravings.", "Y"))
        msgList.add(Messages(0, "Get enough sleep: lack of sleep can increase stress levels, so make sure to get enough rest each night.", "Y"))
        msgList.add(Messages(0, "Be patient with yourself: quitting smoking is a process and it takes time, so be kind and understanding with yourself.", "Y"))
        msgList.add(Messages(0, "SMOKING ALTERNATIVES", "Y"))
        msgList.add(Messages(0, "Here are some alternatives to smoking to help you quit.", "Y"))
        msgList.add(Messages(0, "Nicotine replacement therapy: such as patches, gum, lozenges, inhalers, or nasal sprays.", "Y"))
        msgList.add(Messages(0, "Vaping: using an electronic cigarette or vaporizer can provide a similar experience to smoking.", "Y"))
        msgList.add(Messages(0, "Exercise: physical activity can help reduce cravings and manage stress.", "Y"))
        msgList.add(Messages(0,"Hobbies: engage in a new hobby or activity to keep your hands and mind occupied.", "Y"))
        msgList.add(Messages(0, "Relaxation techniques: try deep breathing, meditation, or yoga to manage stress and reduce cravings.", "Y"))
        msgList.add(Messages(0, "Healthy eating: maintain a healthy diet and snack on healthy foods when cravings strike.", "Y"))
        msgList.add(Messages(0, "Support groups: join a support group or online community to connect with others who are quitting smoking.", "Y"))
        msgList.add(Messages(0, "Therapeutic approaches: consider seeking the help of a therapist or counselor to support your quitting journey.", "Y"))
        msgList.add(Messages(0, "Social activities: spend time with friends and family and participate in social activities to reduce stress.", "Y"))
        msgList.add(Messages(0, "Distraction techniques: distract yourself with a book, movie, music, or puzzle to help manage cravings.", "Y"))
        msgList.add(Messages(0, "Exercise: physical activity can help reduce cravings and manage stress.", "Y"))
        msgList.add(Messages(0, "Healthy eating: maintain a healthy diet and snack on healthy foods when cravings strike.", "Y"))
        msgList.add(Messages(0, "Relaxation techniques: try deep breathing, meditation, or yoga to manage stress and reduce cravings.", "Y"))

        msgList.add(Messages(0, "Sleep: make sure you're getting enough sleep, as lack of sleep can increase stress levels.", "R"))
        msgList.add(Messages(0, "Mindfulness: focus on the present moment and be mindful of your thoughts and feelings to help reduce cravings.", "R"))
        msgList.add(Messages(0, "FOR SOMEONE E.G. LOVED ONES IS IT POSSIBLE TO QUIT SMOKING.", "R"))
        msgList.add(Messages(0, "Yes, it is possible for someone to quit smoking, including loved ones. Quitting smoking is a journey that requires determination, patience, and support from loved ones. There are several evidence-based methods for quitting smoking, including nicotine replacement therapy, medication, counseling, and support from quit-smoking programs. A combination of these methods can greatly increase the chances of success. Encouraging a loved one to quit smoking and supporting them through the process can have a significant impact on their health and overall well-being.QUIT SMOKING TIPS", "R"))
        msgList.add(Messages(0, "Here are some tips to help you quit smoking:", "R"))
        msgList.add(Messages(0, "Set a quit date and stick to it.", "R"))
        msgList.add(Messages(0, "Identify and avoid triggers that lead to smoking.", "R"))
        msgList.add(Messages(0, "Find alternative ways to manage stress and anxiety.", "R"))
        msgList.add(Messages(0, "Keep yourself occupied, engage in physical activity or hobbies.", "R"))
        msgList.add(Messages(0, "Seek support from family, friends, or a support group.", "R"))
        msgList.add(Messages(0, "Consider nicotine replacement therapy, such as patches or gum.", "R"))
        msgList.add(Messages(0, "Reward yourself for milestones achieved in quitting smoking.", "R"))
        msgList.add(Messages(0, "Stay positive and focus on the benefits of quitting, such as improved health and increased energy.", "R"))
        msgList.add(Messages(0, "Avoid alcohol, which can trigger the urge to smoke.", "R"))
        msgList.add(Messages(0, "Stay committed and don't give up, quitting smoking is a process and it takes time.", "R"))
        msgList.add(Messages(0, "QUIT SMOKING TIPS WHILE i AM HAPPY.", "R"))
        msgList.add(Messages(0, "Here are some tips to help you quit smoking while staying positive.", "R"))
        msgList.add(Messages(0, "Focus on the benefits: remind yourself of the positive effects quitting smoking will have on your health and wellbeing.", "R"))
        msgList.add(Messages(0, "Find a replacement activity: try to find a new hobby or physical activity to keep yourself occupied and distracted.", "R"))
        msgList.add(Messages(0, "Surround yourself with support: tell your family and friends about your goal to quit and seek their support.", "R"))

        msgList.add(Messages(0, "Focus on the benefits: remind yourself of the positive effects quitting smoking will have on your health and wellbeing", "G"))
        msgList.add(Messages(0, "Find a replacement activity: try to find a new hobby or physical activity to keep yourself occupied and distracted.", "G"))
        msgList.add(Messages(0, "Surround yourself with support: tell your family and friends about your goal to quit and seek their support.","G"))
        msgList.add(Messages(0, "Keep a journal: write down your thoughts and feelings to track your progress and stay motivated.", "G"))
        msgList.add(Messages(0, "Celebrate milestones: reward yourself for reaching certain milestones, such as going a week without smoking.", "G"))
        msgList.add(Messages(0, "Be kind to yourself: quitting smoking is a challenging process, and it's important to be patient and understanding with yourself.", "G"))
        msgList.add(Messages(0, "Visualize success: imagine yourself as a non-smoker and the positive changes quitting will bring to your life.", "G"))
        msgList.add(Messages(0, "Avoid temptations: steer clear of situations that might trigger the urge to smoke.", "G"))
        msgList.add(Messages(0, "Stay active: physical activity can help reduce cravings and boost your mood.", "G"))
        msgList.add(Messages(0, "Don't give up: quitting smoking is a process and it takes time, but with determination and support, you can be successful.", "G"))
        msgList.add(Messages(0, "are some tips to help you quit smoking while managing stress.","R"))
        msgList.add(Messages(0, "Practice stress-management techniques: try relaxation techniques such as deep breathing, meditation, or yoga to manage stress.", "G"))
        msgList.add(Messages(0, "Find a healthy outlet: engage in physical activity or hobbies to channel stress in a positive way.", "G"))
        msgList.add(Messages(0, "Avoid triggers: identify and avoid situations that trigger the urge to smoke, such as alcohol or certain people.", "G"))
        msgList.add(Messages(0, "Seek support: turn to family, friends, or support groups for encouragement and understanding.","G"))

        quoteList = mutableListOf()
        quoteList.add(Quotes(0, "Every time you try to quit smoking you are actually getting closer to staying smoke-free.", "Henry Ford"))
        quoteList.add(Quotes(0, "A cigarette is the only consumer product which when used as directed kills its consumer.", "Gro Brundtland"))
        quoteList.add(Quotes(0, "You're always better off if you quit smoking; it's never too late.", "Loni Anderson"))
        quoteList.add(Quotes(0, "Today just might be the best day to start seriously thinking about quitting smoking.", "Alexander Woollcott"))
        quoteList.add(Quotes(0, "Giving up smoking is the easiest thing in the world. I know because I've done it thousand times.", "Mark Twain"))
        quoteList.add(Quotes(0, "Quitting smoking might be the hardest thing to do in life, but at least you will have one.", "Anonymous"))

        dropDownMessage = mutableListOf()
        dropDownMessage.add("Stress")
        dropDownMessage.add("Anxiety")
        dropDownMessage.add("Depression")
        dropDownMessage.add("Relaxation")
        dropDownMessage.add("Relieve boredom")
        dropDownMessage.add("Happiness/Short Term Happiness")
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

    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            if(viewModel.isEmpty()) {
                viewModel.insertMessages(msgList = msgList)
            }

            if (viewModel.isEmptyQuotTable()){
                viewModel.insertQuotes(quotes = quoteList)
            }

//            if (viewModel.isEmptyDropDownTable()){
//                viewModel.saveDropDownMessage(dropDownMessage)
//            }
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

//        viewModel.getReason().observe(this, Observer {
//            reasonList = it as ArrayList<Reason>
//        })
//        Log.d("REASON_LIST", "onResume: $reasonList")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.item_edit ->{
                var dialog = Dialog(this@MainActivity)
                dialog.setCancelable(false)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                var lupBinding: LayoutUserProfileBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(dialog.context), R.layout.layout_user_profile, null, false)
                dialog.setContentView(lupBinding.root)
                dialog.show()
                dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                var titleAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, titlesArray)
                var relativeAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, relativeArray)
                lupBinding.dropdownWhom.setAdapter(relativeAdapter)
                lupBinding.titleDropdown.setAdapter(titleAdapter)

                lupBinding.titleDropdown.setOnItemClickListener { parent, view, position, id ->
                    reasonData.title = titleAdapter.getItem(position).toString()
                }

                lupBinding.dropdownWhom.setOnItemClickListener { parent, view, position, id ->
                    reasonData.forWhom = relativeAdapter.getItem(position).toString()
                }

                lupBinding.ivClose.setOnClickListener {
                    dialog.dismiss()
                }
                lupBinding.btnSubmit.setOnClickListener {
                    if(lupBinding.titleDropdown.text.isNotEmpty() || lupBinding.tilName.editText!!.text.isNotEmpty() ||
                            lupBinding.titleDropdown.text.isNotEmpty() || lupBinding.tilForWhom.editText!!.text.isNotEmpty()){
                            reasonData.name = lupBinding.tilName.editText!!.text.toString()
                            reasonData.whomName = lupBinding.etWhomName.editText!!.text.toString()
                        dialog.dismiss()

                    }else{
                        Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getSystemDate(): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        return simpleDateFormat.format(Date())
    }
}