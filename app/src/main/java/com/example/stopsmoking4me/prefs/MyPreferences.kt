package com.example.stopsmoking4me.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

object MyPreferences {
    private lateinit var prefs : SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private const val PREFS_NAME = "stop_smoking"
    private const val IS_PURCHASED_No : String = "is_purchased"
    private const val BASE64 = "base64"
    private const val TITLE = "_title"
    private const val TITLE_NAME = "_title_name"
    private const val WHOM = "_whom"
    private const val WHOM_NAME = "_whom_name"
    private const val IS_FIRST_LAUNCH = "_is_first_launch"
    private var isFirstlaunch: Boolean = false
//    val firstTimeLaunch: Long = 0
    var installationDate by Delegates.notNull<Long>()


    fun init(context: Context){
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
//        editor.commit()

//        isFirstlaunch = prefs.getBoolean(IS_FIRST_LAUNCH, true)
//        firstTimeLaunch = prefs.getLong("first_launch_time", 0)
        installationDate = prefs.getLong(IS_FIRST_LAUNCH, 0L)
//        if (installationDate == 0L){
//            editor.putLong(IS_FIRST_LAUNCH, System.currentTimeMillis())
//        }else{
//
//        }
        editor.commit()
    }

//    private var isFirstlaunch: Boolean = prefs.getBoolean(IS_FIRST_LAUNCH, true)
//    var firstTimeLaunch = prefs.getLong("first_launch_time", 0)
    public fun isFirstLaunch(){
        if (installationDate == 0L){
//            editor.putLong("first_launch_time", System.currentTimeMillis())
//            editor.putBoolean(IS_FIRST_LAUNCH, false)
            editor.putLong(IS_FIRST_LAUNCH, System.currentTimeMillis())
            editor.commit()
        }
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

    fun saveTitle(title: String){
        editor.putString(TITLE, title)
        editor.commit()
    }

    fun getTitle(): String?{
        return prefs.getString(TITLE, "")
    }

    fun saveTitleName(titleName: String){
        editor.putString(TITLE_NAME, titleName)
        editor.commit()
    }

    fun getTitleName(): String?{
        return prefs.getString(TITLE_NAME, "")
    }

    fun saveWhom(whom: String){
        editor.putString(WHOM, whom)
        editor.commit()
    }

    fun getWhom(): String?{
        return prefs.getString(WHOM, "")
    }

    fun saveWhomName(whomeName: String){
        editor.putString(WHOM_NAME, whomeName)
        editor.commit()
    }

    fun getWhomName(): String?{
        return prefs.getString(WHOM_NAME, "")
    }
}