package com.beongaedoctor.beondoc

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class MemberList(
    @SerializedName("data")
    val memberList : List<Member>
)


data class Member (
    @SerializedName("name")
    var name : String,
    @SerializedName("id")
    var id : Long,
    @SerializedName("age")
    var age : String,
    @SerializedName("height")
    var height : String,
    @SerializedName("weight")
    var weight : String,
    @SerializedName("gender")
    var gender : Long
        )



interface MemberService {
    @GET("api/members")
    fun getProfile() : Call<Member>

    @GET("api/members")
    fun getAllProfile() : Call<MemberList>

    @GET("api/members")
    fun getProfile(@Path("id") id : Long) : Call<Member>

    @POST("api/members")
    fun setProfile(@Body profile: Member) : Call<Member>

}


data class Login(
    @SerializedName("email")
    var email : String,
    @SerializedName("password")
    var password : String
)

interface LoginService {
    @POST("로그인경로")
    fun requestLogin(@Body data: Login) : Call<Login>

}