package com.beongaedoctor.beondoc


import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable
import java.time.LocalDateTime


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
        ) : Serializable


data class UpdateMember (
    @SerializedName("loginId")
    var loginId: String = "",
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
    //SerializedName("password")
    //var password: String = "",
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


interface MemberService {
    @POST("api/members")
    fun setProfile(@Body profile: Member) : Call<Member>

    @PUT("api/members/{id}")
    fun reviseProfile(@Path("id") id : Long
                      ,@Body member: UpdateMember) : Call<UpdateMemberResponse>

    @DELETE("api/members/{id}")
    fun deleteProfile(@Path("id") id : Long) : Call<Unit>

}


data class Login(
    @SerializedName("loginId")
    var loginId : String,
    @SerializedName("password")
    var password : String
)



data class LoginResponse(
    @SerializedName("id")
    var id : Long = 0,
    @SerializedName("loginId")
    var loginId: String = "",
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

interface LoginService {
    @POST("api/login")
    fun requestLogin(@retrofit2.http.Body data: Login) : Call<LoginResponse>
}


data class Diagnosis(
    @SerializedName("id")
    var id: Long,
    @SerializedName("diseaseName")
    var diseaseName : String,
    @SerializedName("percent")
    var percent : String, //통짜 확률
    @SerializedName("localDate")
    var localDate : String
)

data class DiagnosisList(
    @SerializedName("data")
    val diagnosisList : List<List<Diagnosis>>
)

data class Diagnosis2DB(
    @SerializedName("diseaseName1")
    var diseaseName1: String?,
    @SerializedName("diseaseName2")
    var diseaseName2: String?,
    @SerializedName("diseaseName3")
    var diseaseName3: String?,
    @SerializedName("percent")
    var percent: String?
)

data class DiagnosisNotID(
    @SerializedName("diseaseId")
    var diseaseId : Long,
    @SerializedName("diseaseName")
    var diseaseName : String,
    @SerializedName("percent")
    var percent : String //단일 확률
)

data class DiagnosisNotIDList(
    @SerializedName("data")
    val diseaseList : List<DiagnosisNotID>
)



interface DiagnosisService{
    //모든 진단 기록 조회
    @GET("api/diagnosisList/{id}")
    fun getDiagnosisRecord(@Path("id") id:Long) : Call<DiagnosisList>

    //DB에 저장됨 (초회 진단 결과)
    @POST("api/diagnosis/{id}")
    fun recordDiagnosis2DB(@Path("id") id:Long, @Body D2DB : Diagnosis2DB) : Call<DiseaseList>


    //DB에 저장 안 됨 (진단 기록 조회 시)
    @POST("api/diagnosis2/{id}")
    fun accessDiagnosis2DB(@Path("id") id:Long, @Body D2DB : Diagnosis2DB) : Call<DiseaseList>

    @GET("api/diagnosisByOne/{id}")
    fun getDiagnosisByOne(@Path("id") id:Long) : Call<List<DiagnosisNotID>>

}

data class DN(
    @SerializedName("diseaseName")
    var diseaseName: String
)

data class Disease(
    @SerializedName("name")
    var name : String,
    @SerializedName("info")
    var info : String,
    @SerializedName("department")
    var department : String,
    @SerializedName("cause")
    var cause : String,
    @SerializedName("symptom")
    var symptom : String
)

data class DiseaseList(
    @SerializedName("diagnosisDTOV2List")
    var diseaseList : List<Disease>
)



interface DiseaseService{
    @GET("api/disease/{id}")
    fun getDiseasebyID(@Path("id") diseaseid : Long) : Call<Disease>

    @POST("api/disease/byString")
    fun getDiseasebyString(@Body diseaseName: DN) : Call<Disease>
}

data class ChatResponse(
    @SerializedName("test")
    var test : List<String>
)

interface ChatResponseService{
    @POST("test")
    fun sendResponse2Model(@Body test: ChatResponse) : Call<List<String>>
}