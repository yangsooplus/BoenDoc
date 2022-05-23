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
    val item: List<HItem2>
)

@Xml
data class HItem(
    @PropertyElement(name = "rnum")
    var rnum : Int?,
    @PropertyElement(name = "distance")
    var distance : Double?,
    @PropertyElement(name = "dutyAddr")
    var dutyAddr : String?,
    @PropertyElement(name = "dutyDiv")
    var dutyDiv : String?,
    @PropertyElement(name = "dutyDivName")
    var dutyDivName : String?,
    @PropertyElement(name = "dutyEmcls")
    var dutyEmcls : String?,
    @PropertyElement(name = "dutyLvkl")
    var dutyLvkl : Int?,
    @PropertyElement(name = "dutyName")
    var dutyName : String?,
    @PropertyElement(name = "dutyTel1")
    var dutyTel1 : String?,
    @PropertyElement(name = "endTime")
    var endTime : Int?,
    @PropertyElement(name = "hpid")
    var hpid : String?,
    @PropertyElement(name = "latitude")
    var latitude : Double?,
    @PropertyElement(name = "longitude")
    var longitude : Double?,
    @PropertyElement(name = "startTime")
    var startTime : Int?,
    @PropertyElement(name = "cnt")
    var cnt : Int?
)

@Xml
data class HItem2(
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