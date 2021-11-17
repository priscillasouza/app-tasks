package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.repository.local.SecurityPreferences

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mSharePreferences = SecurityPreferences(application)

    // MutableLiveData para conseguir observar a Activity
    private val mUserName = MutableLiveData<String>()
    var userName: LiveData<String> = mUserName


    fun loadUserName() {
        mUserName.value = mSharePreferences.get(TaskConstants.SHARED.PERSON_NAME)
    }
}