package com.beongaedoctor.beondoc

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.time.LocalDateTime

data class MemberList(
    @SerializedName("data")
    val memberList : List<Member>
)


data class Member (
    @SerializedName("id")
    var id : Long = 0,
    @SerializedName("loginId")
    var loginId: String = "",
    @SerializedName("password")
    var password: String = "",
    @SerializedName("name")
    var name : String = "",
    @SerializedName("age")
    var age : String = "",
    @SerializedName("height")
    var height : String = "",
    @SerializedName("weight")
    var weight : String = "",
    @SerializedName("gender")
    var gender : Long = 0
        )



interface MemberService {
    @GET("api/members")
    fun getProfile() : Call<Member>

    @GET("api/members")
    fun getAllProfile() : Call<MemberList>

    @GET("api/members/{id}")
    fun getProfile(@Path("id") id : Long) : Call<Member>

    @POST("api/members")
    fun setProfile(@Body profile: Member) : Call<Member>

    @PUT("api/members/{id}")
    fun reviseProfile(@Path("id") id : Long, @Body profile: Member) : Call<Member>
}


data class Login(
    @SerializedName("loginId")
    var loginId : String,
    @SerializedName("password")
    var password : String
)


interface LoginService {
    @POST("api/login")
    fun requestLogin(@Body data: Login) : Call<LoginResponse>

}

data class LoginResponse(
    @SerializedName("member")
    var member : Member
    )


data class Disease(
    @SerializedName("id")
    var id: Long,
    @SerializedName("name")
    var name : String,
    @SerializedName("info")
    var info : String,
    @SerializedName("level")
    var level : Int,
    @SerializedName("department")
    var department : String
)

data class DiagnosisDisease(
    @SerializedName("id")
    var id : Long,
    @SerializedName("Disease")
    var Disease : Disease
)

data class Diagnosis(
    @SerializedName("id")
    var id: Long,
    @SerializedName("Member")
    var Member : Member,
    @SerializedName("DiagnosisDiseases")
    var DiagnosisDiseases : List<DiagnosisDisease>,
    @SerializedName("DiagnosisDate")
    var DiagnosisDate : LocalDateTime,
    @SerializedName("gender")
    var gender: Long
)


data class DiagnosisResponse(
    @SerializedName("id")
    var id : Long,
    @SerializedName("name")
    var name : String,
    @SerializedName("disease1")
    var disease1 : String,
    @SerializedName("info1")
    var info1 : String,
    @SerializedName("disease2")
    var disease2 : String,
    @SerializedName("info2")
    var info2 : String,
    @SerializedName("disease3")
    var disease3 : String,
    @SerializedName("info3")
    var info3 : String
)

interface DiagnosisService{
    @POST("/diagnosis/{id}")
    fun searchDiseasebyString(@Path("id") id:Long, @Body dis1:String,@Body dis2:String,@Body dis3:String) : Call<DiagnosisResponse>
}
