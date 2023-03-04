package com.example.stopsmoking4me.prefs

import android.content.Context
import android.content.SharedPreferences

object MyPreferences {
    private lateinit var prefs : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private const val PREFS_NAME = "stop_smoking"
    private const val IS_PURCHASED_No : String = "is_purchased"
    private const val BASE64 = "base64"

    fun init(context: Context){
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
        editor.commit()
    }

    public fun savePurchaseValueToPref(b : Boolean){
        editor.putBoolean(IS_PURCHASED_No, b)
        editor.commit()
    }

    fun isPurchased() : Boolean{
        return prefs.getBoolean(IS_PURCHASED_No, false)
    }

    public fun saveImageTOBase64(base64: String){
        editor.putString(BASE64, base64)
        editor.commit()
    }

    fun getImageFromBase64(): String?{
        return prefs.getString(BASE64, "")
    }
}