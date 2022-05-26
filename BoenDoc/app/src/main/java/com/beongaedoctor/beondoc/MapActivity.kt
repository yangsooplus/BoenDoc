package com.beongaedoctor.beondoc


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.beongaedoctor.beondoc.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


//말풍선에 붙일 클래스
class BallonInfo (
    val id : String,
    val phone : String,
    val address : String
        )


class MapActivity : AppCompatActivity(){

    // 전역 변수로 바인딩 객체 선언
    private var mapbinding: ActivityMapBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mapbinding!!

    //https://mechacat.tistory.com/14
    private val ACCESS_FINE_LOCATION = 1000     // Request Code
    private var mapView : MapView? = null

    //kakao local api
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 592c80e46b016a87334cf9531a369a6a" // REST API 키
        const val HKEY = "DFKY0ENCJZDVdxX4ulcZ8QSKuf1IYa6pG3TASfba0vk8Nv9DnW19C/nEftvSsZJAIwGWcSEXfP/pdXmNkKQJBQ=="

    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    private var x : String? = null //현재 위치 x좌표.
    private var y : String? = null //현재 위치 y좌표. null인 경우는 못 불러온 경우임.

    private val eventListener = MarkerEventListener(this)   // 마커 클릭 이벤트 리스너

    lateinit var mapKeyword : String

    var currentTime : Int = 0
    var currentDay : String = ""
    var nearHospitalList = mutableListOf<String>()

    var dialog : LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapbinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapKeyword = intent.getStringExtra("mapKeyword").toString()
        dialog = LoadingDialog(this)

        mapView = MapView(this) //맵 뷰 생성
        binding.mapview.addView(mapView)

        //https://fre2-dom.tistory.com/134?category=949323
        //위치정보 권한 요청
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //커스텀 말풍선 등록
        mapView!!.setCalloutBalloonAdapter(CustomBallonAdapter(layoutInflater, this))
        mapView!!.setPOIItemEventListener(eventListener)  // 마커 클릭 이벤트 리스너 등록

        val curTime = System.currentTimeMillis() // ms로 반환
        val timeFormat = SimpleDateFormat("HHmm") // 시(0~23) 분 초
        val eFormat = SimpleDateFormat("E", Locale("ko", "KR"))
        currentTime = timeFormat.format(curTime).toInt()
        currentDay = eFormat.format(curTime)
        println(currentTime)
        println(currentDay)



    }



    override fun onStart() {
        super.onStart()
        binding.mapText.text = "가까운 $mapKeyword 입니다.\n실제 접수 마감 시간과는 차이가 있을 수 있으니 방문 전에 해당 기관에 문의하시기 바랍니다."

        if (checkLocationService()) { // GPS가 켜져있을 경우

            startLocationUpdates() //사용자 현재 위치 추적
            permissionCheck() //위치 권한 체크

        } else { // GPS가 꺼져있을 경우
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }


        binding.gotoMain.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

    private fun startLocationUpdates() {

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 getLastLocation()에 전달
            getLastLocation(locationResult.lastLocation)
        }
    }


    private fun getLastLocation(location: Location) { //주소 값 반환
        x = location.longitude.toString() //현재 x좌표
        y = location.latitude.toString() //현재 y좌표

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startTrackingOn31()
        }

        //현재 좌표를 설정한 뒤 키워드 검색 결과를 받아옴
        Handler().postDelayed({
            //dialog?.show()
            if (mapKeyword.contains(',')) //진료과 여러개가 추천된 경우 {
            {

                //getHospital()

                searchKeyword(splitMapKeyword(mapKeyword), x!!, y!!)
            }

            else
                searchKeyword(mapKeyword, x!!, y!!)
        }, 1000)
    }

    private fun splitMapKeyword(keyword: String) : List<String> = keyword.split(',')



    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
         {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_FINE_LOCATION)

                } else {
                    // 다시 묻지 않음 클릭 (앱 정보 화면으로 이동)
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->

                    }
                    builder.show()
                }
            }
        }
        else {
            // 권한이 있는 상태
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Toast.makeText(this, "31이상은 다른 방법으로",Toast.LENGTH_LONG).show()
                //startTrackingOn31()
            }
            else {
                startTracking() //카카오맵 위치 트래킹 시작
                }
        }
    }

    private fun startTrackingOn31() {
        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(y!!.toDouble(), x!!.toDouble()), true)

        val marker = MapPOIItem()
        marker.apply{
            itemName = "현위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(y!!.toDouble(), x!!.toDouble())
            marker.markerType = MapPOIItem.MarkerType.RedPin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        }
        mapView!!.addPOIItem(marker)
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                //setCurrLocation()
                startTracking()
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치추적 시작
    private fun startTracking() {
        mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }

    // 위치추적 중지
    private fun stopTracking() {
        mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
    }

    //https://jee00609.github.io/android/KakaoMap-With-SeoulAPI/
    //https://apis.map.kakao.com/android/documentation/#MapView_CurrentLocationEventListener_Methods_onCurrentLocationUpdate



    //https://mechacat.tistory.com/15?category=449793
    private fun searchKeyword(keyword: String, x:String, y:String, radius:Int = 1500) {
        val retrofitMap = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofitMap.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, x, y, radius)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                println("단일 진료과 검색")
                //drawMapMarker(response.body()!!) //통신 결과를 마커로 뿌려주기
                if (response.body()!!.meta.total_count > 0) {
                    getHospital(response.body()!!.documents)
                    dialog!!.dismiss()
                }


            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

    private fun searchKeyword(keywords: List<String>, x:String, y:String, radius:Int = 1000) {
        val retrofitMap = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofitMap.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성

        println(keywords)

        for (keyword in keywords) {
            println("검색 : " + keyword)
            val call = api.getSearchKeyword(API_KEY, keyword, x, y, radius)   // 검색 조건 입력

            // API 서버에 요청
            call.enqueue(object: Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    println("여러 진료과 검색")
                    //drawMapMarker(response.body()!!) //통신 결과를 마커로 뿌려주기
                    if (response.body()!!.meta.total_count > 0)
                        getHospital(response.body()!!.documents)

                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    // 통신 실패
                    Log.w("MainActivity", "통신 실패: ${t.message}")
                }
            })

        }

    }

    fun getHospital(searchResult : List<Place>) {
        // timeout setting 해주기
        // timeout setting 해주기
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()


        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofit_h = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B552657/HsptlAsembySearchService/")
            .client(okHttpClient)
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .build()

        val hospitalAPI = retrofit_h.create(HospitalAPI::class.java)

        for (place in searchResult) {
            val placeSplit = place.address_name.split(" ")
            var Q0 = ""
            for (split in placeSplit) {
                if (split.contains("시")) {
                    Q0 = split
                    println(Q0)
                    break
                }
            }

            hospitalAPI!!.getHospitalInfobyName(Q0, place.place_name,1, 10, HKEY).enqueue(object : Callback<Hospital>{
                override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                    if (response.isSuccessful) {

                        drawMapMarker2(place, response.body()!!.body.items.item.get(0))
                        dialog?.dismiss()
                    }

                }

                override fun onFailure(call: Call<Hospital>, t: Throwable) {
                    println(t.message)
                }
            })
        }





    }


    private fun drawMapMarker(response: ResultSearchKeyword) {
        for (place in response.documents) {
            val marker = MapPOIItem()


            if (mapKeyword.equals("약국")) {
                marker.apply {
                    itemName = place.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = R.drawable.markerpharm
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                    customSelectedImageResourceId = R.drawable.markerpharmsele       // 클릭 시 커스텀 마커 이미지
                    isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                    setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                    userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                }
            }
            else {
                if (place.place_name in nearHospitalList) {
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.markerhos
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                        customSelectedImageResourceId = R.drawable.markerhosele       // 클릭 시 커스텀 마커 이미지
                        isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                        setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                        userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                    }
                }
                else {
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.markerhosele
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                        customSelectedImageResourceId = R.drawable.markerhos       // 클릭 시 커스텀 마커 이미지
                        isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                        setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                        userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                    }
                }


            }

            mapView!!.addPOIItem(marker)
        }

    }


    private fun drawMapMarker2(place: Place, hitem: HItem2?) {
            val marker = MapPOIItem()

            if (mapKeyword.equals("약국")) {
                marker.apply {
                    itemName = place.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                    markerType = MapPOIItem.MarkerType.CustomImage
                    customImageResourceId = R.drawable.markerpharm
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                    customSelectedImageResourceId = R.drawable.markerpharmsele       // 클릭 시 커스텀 마커 이미지
                    isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                    setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                    userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                }
            }
            else {

                if (hitem != null && isOperate(hitem)) {
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.markerhos
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                        customSelectedImageResourceId = R.drawable.markerhosele       // 클릭 시 커스텀 마커 이미지
                        isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                        setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                        userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                    }
                }
                else {
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.disablemarker
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                        customSelectedImageResourceId = R.drawable.markerhosele       // 클릭 시 커스텀 마커 이미지
                        isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
                        setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                        userObject = BallonInfo(place.id ,place.phone, place.road_address_name) //커스텀 오브젝트 붙여주기
                    }
                }


            }
            mapView!!.addPOIItem(marker)
    }


    private fun isOperate(hitem: HItem2) : Boolean {
        when(currentDay) {
            "월" -> {
                return currentTime >= hitem.dutyTime1s!! && currentTime <= hitem.dutyTime1c!!
            }
            "화" -> {
                return currentTime >= hitem.dutyTime2s!! && currentTime <= hitem.dutyTime2c!!
            }
            "수" -> {
                return currentTime >= hitem.dutyTime3s!! && currentTime <= hitem.dutyTime3c!!
            }
            "목" -> {
                return currentTime >= hitem.dutyTime4s!! && currentTime <= hitem.dutyTime4c!!
            }
            "금" -> {
                return currentTime >= hitem.dutyTime5s!! && currentTime <= hitem.dutyTime5c!!
            }
            "토" -> {
                return currentTime >= hitem.dutyTime6s!! && currentTime <= hitem.dutyTime6c!!
            }
            "일" -> {
                return currentTime >= hitem.dutyTime7s!! && currentTime <= hitem.dutyTime7c!!
            }
            else -> return false
        }
    }


    class CustomBallonAdapter(inflater: LayoutInflater, context: Context): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.customballon, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.placename)
        val phone: TextView = mCalloutBalloon.findViewById(R.id.placephone)
        val address: TextView = mCalloutBalloon.findViewById(R.id.placeaddress)


        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            //마커 클릭시 나오는 말풍선
            if (poiItem?.userObject != null) {
                val ballonInfo :BallonInfo = poiItem?.userObject as BallonInfo
                name.text = poiItem?.itemName
                phone.text = ballonInfo.phone
                address.text = ballonInfo.address
            }
            else {
                name.text = poiItem?.itemName
                phone.text = ""
                address.text = ""
            }

            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            //말풍선 클릭 시 실행됨
            return mCalloutBalloon
        }



    }


    class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            if (poiItem?.userObject != null) {
                val ballonInfo :BallonInfo = poiItem?.userObject as BallonInfo
                val urladdress = "https://place.map.kakao.com/" + ballonInfo.id
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urladdress))
                startActivity(context, intent, null)

            }
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

        }
    }


}
