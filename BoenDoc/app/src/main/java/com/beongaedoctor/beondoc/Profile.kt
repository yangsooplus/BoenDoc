package com.beongaedoctor.beondoc

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class Profile (
    var name : String,
    var id : Int,
    var age : String,
    var height : String,
    var weight : String
        )


interface ProfileService {
    @GET("api/v2/members")
    fun getProfile() : Call<Profile>

    @GET("api/v2/members")
    fun getAllProfile() : Call<List<Profile>>

    @GET("api/v2/members")
    fun getProfile(@Path("id") id : Int) : Call<Profile>

    @POST("api/v2/members")
    fun setProfile(@Body profile: Profile) : Call<Profile>
}