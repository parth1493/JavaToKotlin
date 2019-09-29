package com.tr1.javatokotlin.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private val BASE_URL = "https://api.github.com/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    val githubAPIService: GithubAPIService by lazy {
            retrofit.create(GithubAPIService::class.java)
    }
}
