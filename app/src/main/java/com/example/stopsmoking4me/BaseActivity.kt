package com.example.stopsmoking4me

import android.R
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stopsmoking4me.R.*

open class BaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    open fun showToast(msg: String){
        var layoutInflater= layoutInflater
        val layout: View = layoutInflater.inflate(layout.custom_toast_layout,
            findViewById<ViewGroup>(id.custom_toast_layout)
        )
        var textMsg = layout.findViewById<TextView>(com.example.stopsmoking4me.R.id.tv_msg)
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.setView(layout) //setting the view of custom toast layout
        textMsg.text = msg
        toast.show()
    }
}