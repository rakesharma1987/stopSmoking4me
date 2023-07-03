package com.example.stopsmoking4me.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.stopsmoking4me.db.AppDao
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.model.StopSmoking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

    suspend fun saveDataIntoStopSmoking(data: StopSmoking){
        appDao.saveDataIntoStopSmoking(data)
    }

    fun isEmptyReasonTable(): Boolean{
        return appDao.isEmptyReasonTable()
    }

    fun getReason(value: String): LiveData<List<Reason>>{
        val dateFormate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calender = Calendar.getInstance()
        val endDate = dateFormate.format(calender.time)
        if (value.equals("Last day")) {
            calender.add(Calendar.DAY_OF_YEAR, -1)
        }
        if (value.equals("Last Week")) {
            calender.add(Calendar.DAY_OF_YEAR, -7)
        }
        if (value.equals("Last month")) {
            calender.add(Calendar.DAY_OF_YEAR, -30)
        }
        if (value.equals("Lifetime")) {
            calender.add(Calendar.DAY_OF_YEAR, 0)
        }
        val startDate = dateFormate.format(calender.time)
        Log.d("START_DATE", "startDate: $startDate")
        return appDao.getReason(endDate, startDate)
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