package com.example.stopsmoking4me.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stopsmoking4me.repository.AppRepository
import com.example.stopsmoking4me.viewModel.AppViewModel

class AppFactory(private val appRepository: AppRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)){
            return AppViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown class.")
    }
}