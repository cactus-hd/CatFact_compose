package com.amir.catfact.data

import com.amir.catfact.model.CatFacts
import okhttp3.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("/fact")
    suspend fun getRandomFact(): retrofit2.Response<CatFacts>

}