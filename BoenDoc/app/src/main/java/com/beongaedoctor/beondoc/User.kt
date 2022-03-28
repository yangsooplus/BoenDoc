package com.beongaedoctor.beondoc

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class User (
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var height: Double = 0.0,
    var weight: Double = 0.0,
    var sex: Int = 0,
    var age: Int = 0,
    var anamnesis: List<String> = emptyList<String>()
)

interface UserInterface {
    @GET("user")
    fun getUser(@Query("email") email: String,
                @Query("password") password: String,
                @Query("name") name: String,
                @Query("height") height: Double,
                @Query("weight") weight: Double,
                @Query("sex") sex: Int,
                @Query("age") age: Int,
                @Query("anamnesis") anamnesis: List<String>
                )

}



