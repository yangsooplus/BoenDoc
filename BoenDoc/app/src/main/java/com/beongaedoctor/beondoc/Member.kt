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
    @SerializedName("age :")
    var age : String,
    @SerializedName("height")
    var height : String,
    @SerializedName("weight")
    var weight : String
        )


data class MemberShort (
    @SerializedName("name")
    var name : String,
    @SerializedName("id")
    var id : Long
    )

interface MemberService {
    @GET("api/v2/members")
    fun getProfile() : Call<Member>

    @GET("api/v2/members")
    fun getAllProfile() : Call<MemberList>

    @GET("api/v2/members")
    fun getProfile(@Path("id") id : Long) : Call<Member>

    @POST("api/v2/members")
    fun setProfile(@Body profile: Member) : Call<Member>

    @POST("api/v2/members")
    fun setProfileShort(@Body profile: MemberShort) : Call<MemberShort>

}