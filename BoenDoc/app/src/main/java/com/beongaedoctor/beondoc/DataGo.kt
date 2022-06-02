package com.beongaedoctor.beondoc

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


@Xml(name = "response")
data class Hospital(
    @Element(name = "body")
    val body : HBody,
    @Element(name = "header")
    val header: HHeader
)

@Xml(name="header")
data class HHeader(
    @PropertyElement(name="resultCode")
    val resultCode: Int,
    @PropertyElement(name="resultMsg")
    val resultMsg: String
)

@Xml(name = "body")
data class HBody(
    @Element(name="items")
    val items: HItems,
    @PropertyElement(name="numOfRows")
    val numOfRows: Int,
    @PropertyElement(name="pageNo")
    val pageNo: Int,
    @PropertyElement(name="totalCount")
    val totalCount: Int
)

@Xml(name= "items")
data class HItems(
    @Element(name="item")
    val item: List<HItem>
)

@Xml
data class HItem(
    @PropertyElement(name = "rnum")
    var rnum : Int?,
    @PropertyElement(name = "dutyAddr")
    var dutyAddr : String?,
    @PropertyElement(name = "dutyDiv")
    var dutyDiv : String?,
    @PropertyElement(name = "dutyDivName")
    var dutyDivName : String?,
    @PropertyElement(name = "dutyEryn")
    var dutyEryn : Int?,
    @PropertyElement(name = "dutyEtc")
    var dutyEtc : String?,
    @PropertyElement(name = "dutyInf")
    var dutyInf : String?,
    @PropertyElement(name = "dutyMapimg")
    var dutyMapimg : String?,
    @PropertyElement(name = "dutyName")
    var dutyName : String?,
    @PropertyElement(name = "dutyTel1")
    var dutyTel1 : String?,
    @PropertyElement(name = "dutyTel3")
    var dutyTel3 : String?,

    @PropertyElement(name = "dutyTime1c")
    var dutyTime1c : Int?,

    @PropertyElement(name = "dutyTime2c")
    var dutyTime2c : Int?,

    @PropertyElement(name = "dutyTime3c")
    var dutyTime3c : Int?,

    @PropertyElement(name = "dutyTime4c")
    var dutyTime4c : Int?,

    @PropertyElement(name = "dutyTime5c")
    var dutyTime5c : Int?,

    @PropertyElement(name = "dutyTime6c")
    var dutyTime6c : Int?,

    @PropertyElement(name = "dutyTime7c")
    var dutyTime7c : Int?,

    @PropertyElement(name = "dutyTime8c")
    var dutyTime8c : Int?,


    @PropertyElement(name = "dutyTime1s")
    var dutyTime1s : Int?,
    @PropertyElement(name = "dutyTime2s")
    var dutyTime2s : Int?,
    @PropertyElement(name = "dutyTime3s")
    var dutyTime3s : Int?,
    @PropertyElement(name = "dutyTime4s")
    var dutyTime4s : Int?,
    @PropertyElement(name = "dutyTime5s")
    var dutyTime5s : Int?,
    @PropertyElement(name = "dutyTime6s")
    var dutyTime6s : Int?,
    @PropertyElement(name = "dutyTime7s")
    var dutyTime7s : Int?,
    @PropertyElement(name = "dutyTime8s")
    var dutyTime8s : Int?,

    @PropertyElement(name = "hpid")
    var hpid : String?,
    @PropertyElement(name = "postCdn1")
    var postCdn1 : String?,
    @PropertyElement(name = "postCdn2")
    var postCdn2 : String?,
    @PropertyElement(name = "wgs84Lon")
    var wgs84Lon : Double?,
    @PropertyElement(name = "wgs84Lat")
    var wgs84Lat : Double?
)

interface HospitalAPI {
    @GET("getHsptlMdcncListInfoInqire")
    fun getHospitalInfobyName(
        @Query("Q0") Q0 : String,
        @Query("QN") QN : String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("ServiceKey") ServiceKey : String
    ) : Call<Hospital>
}





@Xml(name = "response")
data class Emergency1(
    @Element(name = "body")
    val body : EBody1,
    @Element(name = "header")
    val header: EHeader
)

@Xml(name="header")
data class EHeader(
    @PropertyElement(name="resultCode")
    val resultCode: Int,
    @PropertyElement(name="resultMsg")
    val resultMsg: String
)

@Xml(name = "body")
data class EBody1(
    @Element(name="items")
    val items: EItems1,
    @PropertyElement(name="numOfRows")
    val numOfRows: Int,
    @PropertyElement(name="pageNo")
    val pageNo: Int,
    @PropertyElement(name="totalCount")
    val totalCount: Int
)


@Xml(name= "items")
data class EItems1(
    @Element(name="item")
    val item: List<EItem1>
)

@Xml
data class EItem1(
    @PropertyElement(name = "rnum") var rnum : Int?,
    @PropertyElement(name = "hpid") var hpid : String?,
    @PropertyElement(name = "phpid") var phpid : String?,
    @PropertyElement(name = "dutyEmcls") var dutyEmcls : String?,
    @PropertyElement(name = "dutyEmclsName") var dutyEmclsName : String?,
    @PropertyElement(name = "dutyAddr") var dutyAddr : String?,
    @PropertyElement(name = "dutyName") var dutyName : String?,
    @PropertyElement(name = "dutyTel1") var dutyTel1 : String?,
    @PropertyElement(name = "dutyTel3") var dutyTel3 : String?,
    @PropertyElement(name = "wgs84Lon") var wgs84Lon : String?,
    @PropertyElement(name = "wgs84Lat") var wgs84Lat : String?
)


@Xml(name = "response")
data class Emergency2(
    @Element(name = "body")
    val body : EBody2,
    @Element(name = "header")
    val header: EHeader
)


@Xml(name = "body")
data class EBody2(
    @Element(name="items")
    val items: EItems2,
    @PropertyElement(name="numOfRows")
    val numOfRows: Int,
    @PropertyElement(name="pageNo")
    val pageNo: Int,
    @PropertyElement(name="totalCount")
    val totalCount: Int
)


@Xml(name= "items")
data class EItems2(
    @Element(name="item")
    val item: List<EItem2>
)


@Xml
data class EItem2(
    @PropertyElement(name = "hpid") var hpid : String?,
    @PropertyElement(name = "dutyName") var dutyName : String?,



    @PropertyElement(name = "postCdn1") var postCdn1 : String?,
    @PropertyElement(name = "postCdn2") var postCdn2 : String?,
    @PropertyElement(name = "dutyAddr") var dutyAddr : String?,
    @PropertyElement(name = "dutyTel1") var dutyTel1 : String?,
    @PropertyElement(name = "dutyTel3") var dutyTel3 : String?,

    @PropertyElement(name = "dutyHayn") var dutyHayn : Int?,
    @PropertyElement(name = "dutyEryn") var dutyEryn : Int?,

    @PropertyElement(name = "MKioskTy25") var MKioskTy25 : String?,
    @PropertyElement(name = "MKioskTy1") var MKioskTy1 : String?,
    @PropertyElement(name = "MKioskTy2") var MKioskTy2 : String?,
    @PropertyElement(name = "MKioskTy3") var MKioskTy3 : String?,
    @PropertyElement(name = "MKioskTy4") var MKioskTy4 : String?,
    @PropertyElement(name = "MKioskTy5") var MKioskTy5 : String?,
    @PropertyElement(name = "MKioskTy6") var MKioskTy6 : String?,
    @PropertyElement(name = "MKioskTy7") var MKioskTy7 : String?,
    @PropertyElement(name = "MKioskTy8") var MKioskTy8 : String?,
    @PropertyElement(name = "MKioskTy9") var MKioskTy9 : String?,
    @PropertyElement(name = "MKioskTy10") var MKioskTy10 : String?,
    @PropertyElement(name = "MKioskTy11") var MKioskTy11 : String?,
    @PropertyElement(name = "wgs84Lon") var wgs84Lon : Double?,
    @PropertyElement(name = "wgs84Lat") var wgs84Lat : Double?,
    @PropertyElement(name = "dgidIdName") var dgidIdName : String?,
    @PropertyElement(name = "hpbdn") var hpbdn : String?,
    @PropertyElement(name = "hpccuyn") var hpccuyn : String?,
    @PropertyElement(name = "hpcuyn") var hpcuyn : String?,
    @PropertyElement(name = "hperyn") var hperyn : String?,
    @PropertyElement(name = "hpgryn") var hpgryn : String?,
    @PropertyElement(name = "hpicuyn") var hpicuyn : String?,
    @PropertyElement(name = "hpnicuyn") var hpnicuyn : String?,
    @PropertyElement(name = "hpopyn") var hpopyn : String?
)

interface EmergencyAPI {
    @GET("getEgytListInfoInqire")
    fun getEmergencybyAdd(
        @Query("Q0") Q0 : String,
        @Query("Q1") Q1 : String,
        @Query("ServiceKey") ServiceKey : String
    ) : Call<Emergency1>

    @GET("getEgytBassInfoInqire")
    fun getEmergencybyID(
        @Query("HPID") HPID : String,
        @Query("ServiceKey") ServiceKey : String
    ) : Call<Emergency2>
}