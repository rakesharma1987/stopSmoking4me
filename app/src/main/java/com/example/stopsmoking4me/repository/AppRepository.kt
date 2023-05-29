package com.example.stopsmoking4me.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stopsmoking4me.db.AppDao
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import java.util.Calendar

class AppRepository(private val appDao: AppDao) {
    suspend fun insertMessages(msgList: List<Messages>){
        appDao.insertMessages(msgList)
    }

    fun getMessages(colorType: String): List<Messages>{
        return appDao.getMessages(colorType)
    }

    fun isEmpty(): Boolean{
        return appDao.isEmpty()
    }

    fun isEmptyQuotesTable(): Boolean{
        return appDao.isEmptyQuotesTable()
    }

    suspend fun insertQuotes(quotes: List<Quotes>){
        appDao.insertQuotes(quotes)
    }

    fun getQuotes(): LiveData<List<Quotes>>{
        return appDao.getQuotes()
    }

    fun deleteQuotes(id: Int){
        appDao.deleteQuotes(id)
    }

    suspend fun saveReason(reason: Reason){
        appDao.saveReason(reason)
    }

    fun isEmptyReasonTable(): Boolean{
        return appDao.isEmptyReasonTable()
    }

    fun getReason(): LiveData<List<Reason>>{
        val calender = Calendar.getInstance()
        calender.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = calender.time
        Log.d("START_DATE", "startDate: $startDate")
        return appDao.getReason(startDate)
    }

    fun getYesCount(): LiveData<Int>{
        return appDao.getYesCount()
    }

    fun getNoCount(): LiveData<Int>{
        return appDao.getNoCount()
    }

    fun getTotalCount(): LiveData<Reason>{
        return appDao.getTotalCount()
    }
}