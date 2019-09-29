package com.tr1.javatokotlin.models


import com.google.gson.annotations.SerializedName

import io.realm.annotations.PrimaryKey

data class Repository(
    var id: Int,
    var name: String?,
    var language: String?,
    @SerializedName("html_url")var htmlUrl: String?,
    var description: String?,
    @SerializedName("stargazers_count") var stars: Int?,
    @SerializedName("watchers_count") var watchers: Int?,
    var forks: Int?,
    var owner: Owner?)