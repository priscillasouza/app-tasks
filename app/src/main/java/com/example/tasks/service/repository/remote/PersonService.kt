package com.example.tasks.service.repository.remote

import com.example.tasks.service.HeaderModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//Interface PersonService define o método associado ao endpoint
interface PersonService {

    //Passando os dados via POST e recebendo um HeaderModel
    @POST("Authentication/Login")
    @FormUrlEncoded
    fun login(@Field("email") email: String,
              @Field("password") password: String
    ): Call<HeaderModel>


}