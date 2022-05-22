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


data class UpdateMember (
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
    fun reviseProfile(@Path("id") id : Long
                      ,@Body member: UpdateMember) : Call<UpdateMemberResponse>

    @DELETE("api/member/{id}")
    fun deleteProfile(@Path("id") id : Long) : Call<Unit>

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

data class DiagnosisList(
    @SerializedName("data")
    val diseaseList : List<DiagnosisRecord>
)

data class DiagnosisRecord(
    @SerializedName("name")
    var name : String,
    @SerializedName("info")
    var info : String,
    @SerializedName("level")
    var level : Int,
    @SerializedName("department")
    var department : String,
    @SerializedName("diagnosisTime")
    var diagnosisTime : String
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


interface DiagnosisRecordService{
    @GET("api/diagnosisList/{id}")
    fun getDiagnosisRecord(@Path("id") id:Long) : Call<DiagnosisList>
}

data class DN(
    @SerializedName("diseaseName")
    var diseaseName: String
)

data class DNID(
    @SerializedName("id")
    var id: Long
)

interface DiagnosisService{
    @POST("api/diagnosis/{id}")
    fun searchDiseasebyString(@Path("id") id:Long, @Body diseaseName:DN) : Call<DNID>

    @GET("api/diagnosisInfo/{id}")
    fun getDiseasebyDNID(@Path("id") id:Long) : Call<DiagnosisRecord>

    @POST("api/diagnosis1/{id}")
    fun getDiseasebyString1(@Path("id") id:Long, @Body diseaseName: DN) : Call<DiagnosisRecord>

    @POST("api/diagnosis2/{id}")
    fun getDiseasebyString2(@Path("id") id:Long, @Body diseaseName: DN) : Call<DiagnosisRecord>
}

data class ChatResponse(
    @SerializedName("test")
    var test : List<String>
)

data class ModelResult(
    @SerializedName("diseaseName")
    var diseaseName: List<String>
    )

interface chatResponseService{
    @POST("test")
    fun sendResponse2Model(@Body test: ChatResponse) : Call<List<String>>
}