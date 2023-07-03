package com.example.stopsmoking4me.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopsmoking4me.model.Reason
import com.example.stopsmoking4me.model.Messages
import com.example.stopsmoking4me.model.Quotes
import com.example.stopsmoking4me.model.StopSmoking
import com.example.stopsmoking4me.repository.AppRepository
import kotlinx.coroutines.launch

class AppViewModel(private val appRepository: AppRepository): ViewModel() {

    fun insertMessages(msgList: List<Messages>){
        viewModelScope.launch {
            appRepository.insertMessages(msgList)
        }
    }

    fun getMessages(colorType: String): List<Messages>{
        return appRepository.getMessages(colorType)
    }

    fun isEmpty(): Boolean{
        return appRepository.isEmpty()
    }

    fun insertQuotes(quotes: List<Quotes>){
        viewModelScope.launch {
            appRepository.insertQuotes(quotes)
        }
    }

    fun getQuotes(): LiveData<List<Quotes>>{
        return appRepository.getQuotes()
    }

    fun isEmptyQuotTable(): Boolean{
        return appRepository.isEmptyQuotesTable()
    }

    fun deleteQuotes(id: Int){
        appRepository.deleteQuotes(id)
    }

    fun saveReason(reason: Reason){
        viewModelScope.launch {
            appRepository.saveReason(reason)
        }
    }

    fun saveDataIntoStopSmoking(data: StopSmoking){
        viewModelScope.launch {
            appRepository.saveDataIntoStopSmoking(data)
        }
    }

    fun isEmptyReasonTable(): Boolean{
        return appRepository.isEmptyReasonTable()
    }

    fun getReason(value: String): LiveData<List<Reason>>{
        return appRepository.getReason(value)
    }

    fun getYesCount(): LiveData<Int>{
        return appRepository.getYesCount()
    }

    fun getNoCount(): LiveData<Int>{
        return appRepository.getNoCount()
    }

    fun getTotalCount(): LiveData<Reason>{
        return appRepository.getTotalCount()
    }


}