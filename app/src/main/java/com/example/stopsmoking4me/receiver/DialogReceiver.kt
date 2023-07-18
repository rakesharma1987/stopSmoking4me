package com.example.stopsmoking4me.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.stopsmoking4me.fragments.MyDialogFragment

class DialogReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        when (intent!!.action){
//            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED ->{
//                val dialogFragment = MyDialogFragment()
//                dialogFragment.show((context as AppCompatActivity).supportFragmentManager, "dialog")
//            }
//        }

        AlertDialog.Builder(context!!)
            .setTitle("Reminder")
            .setMessage("Reminder for money")
            .setPositiveButton("Get Premium", object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    Log.d("WorkManager: ", "onClick: ")
                }

            })
            .show()
    }
}