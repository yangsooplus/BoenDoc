package com.beongaedoctor.beondoc

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable
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
    var gender : Long = 0,
    @SerializedName("drug")
    var drug : String = "",
    @SerializedName("social")
    var social : String = "",
    @SerializedName("family")
    var family : String = "",
    @SerializedName("trauma")
    var trauma : String = "",
    @SerializedName("femininity")
    var femininity : String = ""
    //@SerializedName("basicExam")
    //var basicExam: BasicExam? = null
        ) : Serializable


data class UpdateMemberResponse (
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
    var gender : Long = 0,
    @SerializedName("drug")
    var drug : String = "",
    @SerializedName("social")
    var social : String = "",
    @SerializedName("family")
    var family : String = "",
    @SerializedName("trauma")
    var trauma : String = "",
    @SerializedName("femininity")
    var femininity : String = ""
    //@SerializedName("basicExam")
    //var basicExam: BasicExam? = null
)

data class BasicExam(
    @SerializedName("drug")
    var drug : String = "",
    @SerializedName("social")
    var social : String = "",
    @SerializedName("family")
    var family : String = "",
    @SerializedName("trauma")
    var trauma : String = "",
    @SerializedName("femininity")
    var femininity : String = ""
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
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    fun reviseProfile(@Path("id") id : Long
                      , @Body member: Member) : Call<UpdateMemberResponse>
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

    @POST("api/loginV2")
    fun requestLogin2(@Body data: Login) : Call<Member>

}

data class LoginResponse(
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
    var gender : Long = 0,
    @SerializedName("drug")
    var drug : String = "",
    @SerializedName("social")
    var social : String = "",
    @SerializedName("family")
    var family : String = "",
    @SerializedName("trauma")
    var trauma : String = "",
    @SerializedName("femininity")
    var femininity : String = ""
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
    var info1 : String
)

interface DiagnosisService{
    @POST("api/diagnosis/{id}")
    fun searchDiseasebyString(@Path("id") id:Long, @Body diseaseName1:String) : Call<DiagnosisResponse>
}
