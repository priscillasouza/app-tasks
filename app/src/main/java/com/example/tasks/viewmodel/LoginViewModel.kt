package com.example.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasks.service.model.HeaderModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.helper.FingerprintHelper
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.listener.ValidationListener
import com.example.tasks.service.model.PriorityModel
import com.example.tasks.service.repository.PersonRepository
import com.example.tasks.service.repository.PriorityRepository
import com.example.tasks.service.repository.local.SecurityPreferences
import com.example.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val mPersonRepository = PersonRepository(application)
    private val mPriorityRepository = PriorityRepository(application)
    private val mSharePreferences = SecurityPreferences(application)

    private val mLogin = MutableLiveData<ValidationListener>()
    var login: LiveData<ValidationListener> = mLogin

    private val mFingerprint = MutableLiveData<Boolean>()
    var fingerprint: LiveData<Boolean> = mFingerprint

    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener<HeaderModel> {
            override fun onSuccess(model: HeaderModel) {

                mSharePreferences.store(TaskConstants.SHARED.TOKEN_KEY, model.token)
                mSharePreferences.store(TaskConstants.SHARED.PERSON_KEY, model.personKey)
                mSharePreferences.store(TaskConstants.SHARED.PERSON_NAME, model.name)

                RetrofitClient.addHeader(model.token, model.personKey)

                mLogin.value = ValidationListener()
            }

            override fun onFailure(str: String) {
                mLogin.value = ValidationListener(str)
            }

        })
    }

    fun isAuthenticationAvailable() {
        val tokenKey = mSharePreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val personKey = mSharePreferences.get(TaskConstants.SHARED.PERSON_KEY)

        // Se o token e person key forem diferentes de vazio , usuário está logado
        val everLogged = (tokenKey != "" && personKey != "")

        //Atualiza valores de Header para requisiçõe
        RetrofitClient.addHeader(tokenKey, personKey)

        // Se o usuário não estiver logado, a aplicação vai atualizar os dados
        if(!everLogged) {
            mPriorityRepository.all(object : APIListener<List<PriorityModel>> {
                override fun onSuccess(model: List<PriorityModel>) {
                    mPriorityRepository.save(model)
                }

                override fun onFailure(str: String) {
                }

            })

        }

        if(FingerprintHelper.isAutenticationAvailable(getApplication())) {
            mFingerprint.value = everLogged
        }
    }
}