package com.example.stopsmoking4me.workManager

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stopsmoking4me.fragments.MyDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDialgWorkManager(context: Context, workerParam: WorkerParameters): CoroutineWorker(context, workerParam) {
    override suspend fun doWork(): Result {
        try {
//            val dialogFragment = MyDialogFragment()
//            dialogFragment.show((applicationContext as AppCompatActivity).supportFragmentManager, "dialog")
            withContext(Dispatchers.Main){
                AlertDialog.Builder(applicationContext)
                    .setTitle("Reminder")
                    .setMessage("Reminder for money")
                    .setPositiveButton("Get Premium", object: DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Log.d("WorkManager: ", "onClick: ")
                        }

                    })
                    .show()
            }
            return Result.success()
        }catch (e: Throwable){
            Log.d("PERIODIC_WORK:", "doWork: ${e.message}")
            return Result.failure()
        }

    }
}