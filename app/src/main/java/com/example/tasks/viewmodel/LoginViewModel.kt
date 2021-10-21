package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.local.SecurityPreferences

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mPersonRepository = PersonRepository(application)
    private val mSharePreferences = SecurityPreferences(application)

    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    private val mLoggedUser = MutableLiveData<Boolean>()
    var loggedUser: LiveData<Boolean> = mLoggedUser

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener {
            override fun onSuccess(model: HeaderModel) {

                mSharePreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharePreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharePreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                mLogin.value = ValidationListener()
            }

            override fun onFailure(str: String) {
                mLogin.value = ValidationListener(str)
            }

        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {
        val token = mSharePreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = mSharePreferences.get(TaskConstants.SHARED.PERSON_KEY)

        val logged = (token != "" && person != "")
        mLoggedUser.value = logged
    }

}