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
    @SerializedName("name")
    var name : String,
    @SerializedName("diagnosisTime")
    var diagnosisTime : String
    //+ id, 확률
)


data class DiagnosisList(
    @SerializedName("data")
    val diseaseList : List<Diagnosis>
)

interface DiagnosisService{
    @GET("api/diagnosisList/{id}")
    fun getDiagnosisRecord(@Path("id") id:Long) : Call<DiagnosisList>
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

/*
data class DN3(
    @SerializedName("diseaseName1")
    var diseaseName1: String,
    @SerializedName("diseaseName2")
    var diseaseName2: String,
    @SerializedName("diseaseName3")
    var diseaseName3: String
)
*/

interface DiseaseService{
    @POST("api/diagnosis/{id}")
    fun getDiseasebyString1(@Path("id") id:Long, @Body diseaseName: DN) : Call<Disease>

    //@POST("api/diagnosis2/{id}")
    //fun getDiseasebyString2(@Path("id") id:Long, @Body diseaseName: DN3) : Call<Disease>

    //@POST("api/diagnosis/{id}")
    //fun getDisease3byString(@Path("id") id:Long, @Body dn : DN3 ) : Call<DR2List>
}

data class ChatResponse(
    @SerializedName("test")
    var test : List<String>
)

interface ChatResponseService{
    @POST("test")
    fun sendResponse2Model(@Body test: ChatResponse) : Call<List<String>>
}