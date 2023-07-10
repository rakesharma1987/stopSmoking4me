package com.example.stopsmoking4me.workManager

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stopsmoking4me.fragments.MyDialogFragment

class MyDialgWorkManager(context: Context, workerParam: WorkerParameters): Worker(context, workerParam) {
    override fun doWork(): Result {
        try {
            val dialogFragment = MyDialogFragment()
            dialogFragment.show((applicationContext as AppCompatActivity).supportFragmentManager, "dialog")
            return Result.success()
        }catch (e: Throwable){
            Log.d("Exception:", "doWork: ${e.message}")
            return Result.failure()
        }

    }
}