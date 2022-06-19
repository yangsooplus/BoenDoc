package com.beongaedoctor.beondoc


import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable
import java.time.LocalDateTime


data class Member (
    @SerializedName("id")
    var id : Long = 0, //DB에서의 유저 id
    @SerializedName("loginId")
    var loginId: String = "", //이메일(로그인 아이디)
    @SerializedName("password")
    var password: String = "", //비밀번호
    @SerializedName("name")
    var name : String = "", //이름
    @SerializedName("age")
    var age : String = "", //나이
    @SerializedName("height")
    var height : String = "", //신장
    @SerializedName("weight")
    var weight : String = "", //체중
    @SerializedName("gender")
    var gender : Long = 0, //성별. 0 - 여성, 1 - 남성.
    @SerializedName("drug")
    var drug : String = "", //약물 투약력
    @SerializedName("social")
    var social : String = "", //사회력
    @SerializedName("family")
    var family : String = "", //가족력
    @SerializedName("trauma")
    var trauma : String = "", //외상력, 과거력
    @SerializedName("femininity")
    var femininity : String = "" //여성력
        ) : Serializable //Intent로 Member 객체를 주고 받기 위해 직렬화.


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

data class Emailcheck(
    @SerializedName("loginId")
    var loginId: String
)

data class PWcheck(
    @SerializedName("password")
    var password: String
)

interface MemberService {
    //새로운 회원 정보 등록
    @POST("api/members")
    fun setProfile(@Body profile: Member) : Call<Member>

    //회원 정보 수정
    @PUT("api/members/{id}")
    fun reviseProfile(@Path("id") id : Long
                      ,@Body member: UpdateMember) : Call<UpdateMemberResponse>

    //회원 정보 삭제
    @DELETE("api/members/{id}")
    fun deleteProfile(@Path("id") id : Long) : Call<Unit>

    //이메일(아이디) 중복 체크
    @POST("api/members/loginIdCheck/")
    fun checkAlreadyID(@Body loginId: Emailcheck) : Call<Int> //이미 있으면 1, 없으면 0

    //비밀번호 일치 여부 체크
    @POST("api/members/pwcheck/{id}")
    fun passwordCheck(@Path("id") id: Long, @Body password: PWcheck) : Call<Int> //같으면 0, 다르면 1

    //비밀번호 수정
    @PUT("api/members/pwupdate/{id}")
    fun passwordUpdate(@Path("id") id: Long, @Body password: PWcheck) : Call<Unit>
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
    var id: Long, //진단 id. 진단 DB에서 id로 조회할 수 있다.
    @SerializedName("diseaseName")
    var diseaseName : String, //질병명
    @SerializedName("percent")
    var percent : String, //한 진단에 나오는 3개의 질병 확률을 공백으로 구분하여 작성
    @SerializedName("localDate")
    var localDate : String //진단 일시
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




interface DiagnosisService{
    //모든 진단 기록 조회
    @GET("api/diagnosisList/{id}")
    fun getDiagnosisRecord(@Path("id") id:Long) : Call<DiagnosisList>

    //진단 결과를 DB에 저장 (초회 진단 결과 수신 시 호출)
    @POST("api/diagnosis/{id}")
    fun recordDiagnosis2DB(@Path("id") id:Long, @Body D2DB : Diagnosis2DB) : Call<DiseaseList>

    //진단 결과를 조회 (기존 진단 기록 조회 시 호출)
    @POST("api/diagnosis2/{id}")
    fun accessDiagnosis2DB(@Path("id") id:Long, @Body D2DB : Diagnosis2DB) : Call<DiseaseList>

    //진단 id로 특정 진단 기록을 조회
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
    //질병 id로 질병 정보 조회
    @GET("api/disease/{id}")
    fun getDiseasebyID(@Path("id") diseaseid : Long) : Call<Disease>

    //질병 이름으로 질병 정보 조회
    @POST("api/disease/byString")
    fun getDiseasebyString(@Body diseaseName: DN) : Call<Disease>
}

data class ChatResponse(
    @SerializedName("string")
    var string : String
)

data class ChatResult(
    @SerializedName("diseaseName1")
    var diseaseName1 : String

)

interface ChatResponseService{
    //Flask의 예측 모델에 사용자의 응답을 전달
    @POST("api/start")
    fun sendResponse2Model(@Body test: ChatResponse) : Call<List<String>>
}