package com.cyberlink.photodirecto.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyberlink.photodirecto.App

class MyViewModelFactory(private val app: App, private val activity: Activity) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyViewModel(app, activity) as T
    }
}