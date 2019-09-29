package com.tr1.javatokotlin.retrofit


import com.tr1.javatokotlin.models.Repository
import com.tr1.javatokotlin.models.SearchResponse

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap


interface GithubAPIService {

    @GET("search/repositories")
    fun searchRepositories(@QueryMap options: Map<String, String>): Call<SearchResponse>

    @GET("/users/{username}/repos")
    fun searchRepositoriesByUser(@Path("username") githubUser: String): Call<List<Repository>>
}

//https://api.github.com/search/repositories?q=mario+language:java

//https://api.github.com//users/parth1493/repos