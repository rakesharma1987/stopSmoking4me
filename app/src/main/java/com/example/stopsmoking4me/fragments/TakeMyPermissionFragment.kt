package com.example.stopsmoking4me.fragments

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stopsmoking4me.MainActivity
import com.example.stopsmoking4me.activity.BillingActivity
import com.example.stopsmoking4me.adapter.MyRecyclerviewAdapter
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.StopSmoking
import com.example.stopsmoking4me.prefs.MyPreferences
import com.example.stopsmoking4me.util.Utility
import com.stopsmokingforfamily.aityl.R
import com.stopsmokingforfamily.aityl.databinding.FragmentTakeMyPermissionBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.Runnable
import java.io.ByteArrayOutputStream
import java.util.*


private const val PICK_IMAGE = 100
class TakeMyPermissionFragment : Fragment(), View.OnClickListener{
    private lateinit var binding: FragmentTakeMyPermissionBinding
//    private lateinit var adRequest: AdRequest
    var list: List<Messages> = mutableListOf<Messages>()
    private lateinit var adapter: MyRecyclerviewAdapter
    lateinit var dropDownReason: String
    var btnYesOrNoClicked:Int = 0

    private var dropDownList = mutableListOf<String>()
    private lateinit var animator1: ObjectAnimator
    private lateinit var animator2: ObjectAnimator

    private val TAKE_MY_PERMISSION = "_takeMyPermission"

    private var stopSmoking: StopSmoking = StopSmoking()
    private var reason: String = ""
    private var smoking: String = ""
    private var TAG = "TakeMyPermissionFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_take_my_permission, container, false)

        if (MyPreferences.getImageFromBase64()!!.isNotEmpty()) {
            val byte = android.util.Base64.decode(
                MyPreferences.getImageFromBase64()!!,
                android.util.Base64.DEFAULT
            )
            val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte.size)
            binding.imageView.setImageBitmap(bitmap)
        }

//        adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)
        dropDownList.addAll((requireContext() as MainActivity).dropDownMessage)
        binding.imageView.setOnClickListener(this)

        binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_green))
        binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_yellow))
        binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_red))

        binding.btnShallGo4Smoke.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnYes.setOnClickListener(this)
        binding.btnNo.setOnClickListener(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MyRecyclerviewAdapter(requireContext(), list)

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dropDownList)
        binding.dropDown.adapter = arrayAdapter
        binding.dropDown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                dropDownReason = parent?.getItemAtPosition(position).toString()
                if ((requireContext() as MainActivity).reasonData.yesOrNo) {
                    (requireContext() as MainActivity).reasonData.dropDownReason = dropDownReason
                    stopSmoking.reason = dropDownReason
                    reason = dropDownReason
                }else{
                    (requireContext() as MainActivity).reasonData.dropDownReason = "no reason"
                    stopSmoking.reason = ""
                    reason = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        binding.btnSubmit.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when(v?.id){
           R.id.image_view ->{
               val gallary = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
               startActivityForResult(gallary, PICK_IMAGE)
           }

            R.id.btn_shall_go_4_smoke ->{
                binding.tvDisplayMsg.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                changeColor()
            }

            R.id.btn_submit ->{
//                if ((requireContext() as MainActivity).reasonData.title.isNullOrEmpty() ||
//                    (requireContext() as MainActivity).reasonData.name.isNullOrEmpty() ||
//                    (requireContext() as MainActivity).reasonData.forWhom.isNullOrEmpty() ||
//                    (requireContext() as MainActivity).reasonData.whomName.isNullOrEmpty()){
//                    Toast.makeText(context, "Please fill details", Toast.LENGTH_SHORT).show()
//                }
                if (MyPreferences.getTitle().isNullOrEmpty() ||
                    MyPreferences.getTitleName().isNullOrEmpty() ||
                    MyPreferences.getWhom().isNullOrEmpty() ||
                    MyPreferences.getWhomName().isNullOrEmpty()){
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                    (requireContext() as MainActivity).showToast("Please fill details")
                }else if(btnYesOrNoClicked == 0){
                    (requireContext() as MainActivity).showToast("Please confirm Are you going for smoking?")
//                    Toast.makeText(context, "Please confirm \n Are you going for smoking?", Toast.LENGTH_SHORT).show()
                }else{
//                    (requireContext() as MainActivity).viewModel.saveReason((requireContext() as MainActivity).reasonData)
//                    (requireActivity() as MainActivity).viewModel.saveDataIntoStopSmoking(stopSmoking)
                    if (reason == ""){
                        (requireContext() as MainActivity).showToast("Please select reason from drop down.")
//                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    }else {
                        (requireActivity() as MainActivity).dbAdapter.saveData(reason, smoking)
                        (requireContext() as MainActivity).showToast("Data saved successfully.")
//                        Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                    }

                    btnYesOrNoClicked = 0 // reset btn yes/no
                }
            }

            R.id.btn_yes ->{
                binding.btnYes.isEnabled = false

                stopSmoking.isSmoking = true
                stopSmoking.dateString = (requireActivity() as MainActivity).getSystemDate()
                stopSmoking.day = android.text.format.DateFormat.format("EEEE", Calendar.getInstance()).toString()
                stopSmoking.hour = Utility().getCurrentTime()

                Handler().postDelayed(object: Runnable{
                    override fun run() {
                        binding.btnYes.isEnabled = true

                        (requireContext() as MainActivity).reasonData.dateString = (requireContext() as MainActivity).getSystemDate()
                        (requireContext() as MainActivity).reasonData.yesOrNo = true
                        btnYesOrNoClicked = btnYesOrNoClicked.plus(1)
                        blinkTextViewReason()
                        stopSmoking.isSmoking = true
                        smoking = "Yes"
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000)
                            binding.tvReason.background = resources.getDrawable(R.drawable.drawable_rectangle_shap)
                            binding.tvReason.setTextColor(resources.getColor(R.color.white))
                            stopBlinkingReason()
                        }
                    }

                }, 5000)

            }

            R.id.btn_no ->{
                if (MyPreferences.getTitle().isNullOrEmpty() ||
                    MyPreferences.getTitleName().isNullOrEmpty() ||
                    MyPreferences.getWhom().isNullOrEmpty() ||
                    MyPreferences.getWhomName().isNullOrEmpty()){
                    (requireContext() as MainActivity).showToast("Please fill details")
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                }else {
                    binding.btnNo.isEnabled = false
                    smoking = "No"

                    Handler().postDelayed(object: Runnable{
                        override fun run() {
                            binding.btnNo.isEnabled = true
                            (requireContext() as MainActivity).reasonData.yesOrNo = false
                            btnYesOrNoClicked = btnYesOrNoClicked.plus(1)

                            stopSmoking.isSmoking = false
                            stopSmoking.dateString = (requireActivity() as MainActivity).getSystemDate()
                            stopSmoking.day = android.text.format.DateFormat.format("EEEE", Calendar.getInstance()).toString()
                            stopSmoking.hour = Utility().getCurrentTime()
                            stopSmoking.reason = ""
                            reason = ""

                            var alertDialog = AlertDialog.Builder(context)
                            alertDialog.setTitle(getString(R.string.app_name))
                            alertDialog.setMessage("You have choosen NO \n Thank You")
                            alertDialog.setPositiveButton("Ok") { dialog, which ->
                                (requireContext() as MainActivity).reasonData.dateString = (requireContext() as MainActivity).getSystemDate()
                                (requireContext() as MainActivity).viewModel.saveReason((requireContext() as MainActivity).reasonData)
                                (requireContext() as MainActivity).viewModel.saveDataIntoStopSmoking(stopSmoking)
                                (requireActivity() as MainActivity).dbAdapter.saveData(reason, smoking)
                                dialog!!.dismiss()
                            }
                            alertDialog.create().show()
                        }

                    }, 100)
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            val imageURI = data?.data
            binding.imageView.setImageURI(imageURI)
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageURI)
            val baos: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byte = baos.toByteArray()
            val base64 = android.util.Base64.encodeToString(byte, android.util.Base64.DEFAULT)
            MyPreferences.saveImageTOBase64(base64)
        }
    }

    private fun changeColor(){
        object: CountDownTimer(10000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.tvDisplayMsg.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
                var value = millisUntilFinished/1000
                var value1 = value.toInt()
                val v741 = arrayOf(7, 4, 1)
                val v852 = arrayOf(8, 5, 2)
                val v963 = arrayOf(9, 6, 3)
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main){
                        if (value1 in v741){
                            binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_green))
                            binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_yellow))
                            binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_red))
                        }
                        if (value1 in v852){
                            binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_green))
                            binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_yellow))
                            binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_red))
                        }
                        if (value1 in v963){
                            binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_green))
                            binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_yellow))
                            binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_red))
                        }
                    }
                }
            }

            override fun onFinish() {
                blinkTextviewAreYouGoingForSmoking()
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    binding.btnAreUGoing4Smoking.background = resources.getDrawable(R.drawable.drawable_rectangle_shap)
                    binding.btnAreUGoing4Smoking.setTextColor(resources.getColor(R.color.white))
                    stopBlinkingAreYouGoingForSmoking()

                }
                binding.tvDisplayMsg.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_green))
                binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_yellow))
                binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_border_red))
                var colorType = ""

                val randomNumber = (1..3).random()
                Log.d("TAG", "onFinish: $randomNumber")
                if (randomNumber == 1){
                    colorType = "G"
                    binding.ivGreen.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_green))
                }
                if (randomNumber == 2){
                    colorType = "Y"
                    binding.ivYellow.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_yellow))
                }
                if (randomNumber == 3){
                    colorType = "R"
                    binding.ivRed.setImageDrawable(requireContext().getDrawable(R.drawable.drawable_circle_red))
                }

                CoroutineScope(Dispatchers.IO).launch {
                    list = (requireContext() as MainActivity).viewModel.getMessages(colorType)
                }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    val rand = (1..list.size).random()
                    val messages = list[rand-1]
                    val randMsgList = mutableListOf<Messages>()
                    randMsgList.add(messages)
                    adapter = MyRecyclerviewAdapter(requireContext(), randMsgList)
                    binding.recyclerView.adapter = adapter
                }
            }
        }.start()
    }

    fun blinkTextviewAreYouGoingForSmoking(){
        animator1 =
            ObjectAnimator.ofInt(binding.btnAreUGoing4Smoking, "backgroundColor", Color.GREEN, Color.RED, Color.BLUE)
        animator1.duration = 500
        animator1.setEvaluator(ArgbEvaluator())
        animator1.repeatCount = Animation.ABSOLUTE
        animator1.repeatCount = Animation.ABSOLUTE
        animator1.start()
    }

    fun blinkTextViewReason(){
        animator2 =
            ObjectAnimator.ofInt(binding.tvReason, "backgroundColor", Color.GREEN, Color.RED, Color.BLUE)
        animator2.duration = 1000
        animator2.setEvaluator(ArgbEvaluator())
        animator2.repeatCount = Animation.ABSOLUTE
        animator2.repeatCount = Animation.ABSOLUTE
        animator2.start()
    }

    fun stopBlinkingAreYouGoingForSmoking(){
        animator1.cancel()
    }

    fun stopBlinkingReason(){
        animator2.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
//        if ((requireArguments() as MainActivity).hasTwoDaysPassed()){
//            AlertDialog.Builder(requireContext())
//                .setTitle("Reminder")
//                .setMessage("Reminder for money")
//                .setPositiveButton("Get Premium", object: DialogInterface.OnClickListener{
//                    override fun onClick(dialog: DialogInterface?, which: Int) {
//                        Log.d("WorkManager: ", "onClick: ")
//                    }
//
//                })
//                .create()
//                .show()


            if ((requireContext() as MainActivity).hasTwoDaysPassed()) {
                if (!MyPreferences.isPurchased()){
                    var alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Reminder")
                    alertDialog.setMessage("Reminder for money")
                    alertDialog.setCancelable(false)
                    alertDialog.setPositiveButton("Get Premium", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            context?.startActivity(Intent(activity?.applicationContext, BillingActivity::class.java))
                        }

                    })
                    alertDialog.setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }

                    })

        alertDialog.create().show()
    }
}else{
    (requireContext() as MainActivity).setRepeatingAlarm(requireContext())
}

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }
}