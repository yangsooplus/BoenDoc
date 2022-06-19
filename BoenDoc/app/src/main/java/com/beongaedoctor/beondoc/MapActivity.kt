package com.beongaedoctor.beondoc


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.beongaedoctor.beondoc.App.Companion.context
import com.beongaedoctor.beondoc.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices
import com.sothree.slidinguppanel.SlidingUpPanelLayout
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
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.Boolean.FALSE
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


//말풍선에 붙일 클래스
class BallonInfo (
    val type : String, //병원(Hos), 약국(Pharm), 응급실(Egy) 3가지 종류
    val id : String, //카카오 로컬, 지도의 장소 id.
    val phone : String, //전화번호
    val address : String, //주소
    var eItem2: EItem2? //응급실의 경우 응급실 정보. 병원, 약국의 경우에는 null -> nullable 선언
        )


class MapActivity : AppCompatActivity(){
    // 전역 변수로 바인딩 객체 선언
    private var mapbinding: ActivityMapBinding? = null
    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mapbinding!!

    private val ACCESS_FINE_LOCATION = 1000  //정확한 위치 접근을 위한 Request Code
    private var mapView : MapView? = null //지도에 해당하는 MapView

    //api Url과 Key
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/" //Kakao 지도/로컬 통합 url
        const val API_KEY = "KakaoAK 592c80e46b016a87334cf9531a369a6a" // REST API 키
        const val H_BASE_URL = "http://apis.data.go.kr/B552657/HsptlAsembySearchService/" //의료기관 url
        const val HKEY = "DFKY0ENCJZDVdxX4ulcZ8QSKuf1IYa6pG3TASfba0vk8Nv9DnW19C/nEftvSsZJAIwGWcSEXfP/pdXmNkKQJBQ==" //의료기관 api 키
        const val E_BASE_URL = "http://apis.data.go.kr/B552657/ErmctInfoInqireService/" //응급의료기관 url
        const val EKEY = "DFKY0ENCJZDVdxX4ulcZ8QSKuf1IYa6pG3TASfba0vk8Nv9DnW19C/nEftvSsZJAIwGWcSEXfP/pdXmNkKQJBQ==" //응급의료기관 api 키
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    private lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는 변수

    private var x : String? = null //현재 위치 x좌표.
    private var y : String? = null //현재 위치 y좌표. null인 경우는 못 불러온 경우임.

    lateinit var eventListener : MarkerEventListener   // 마커 클릭 이벤트 리스너

    lateinit var mapKeyword : String //병원 -> 검색 키워드, 약국 -> "Pharm", 응급실 -> "Egy"

    var currentTime : Int = 0 //현재 시간 (기기의 시간을 따름)
    var currentDay : String = "" //요일 (기기의 요일을 따름)

    var dialog : LoadingDialog? = null //로딩창


    lateinit var currAddress: List<Address> //현재 주소
    var STAGE1 = "" //OO시
    var STAGE2 = "" //OO구

    lateinit var slidePanel : SlidingUpPanelLayout //응급실 정보를 표시할 슬라이딩패널

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapbinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapKeyword = intent.getStringExtra("mapKeyword").toString() //검색 키워드를 받아온다
        dialog = LoadingDialog(this) //로딩창 생성

        mapView = MapView(this) //맵 뷰 생성
        binding.mapview.addView(mapView) //레이아웃에 맵뷰를 추가하여 지도를 출력한다.

        //https://fre2-dom.tistory.com/134?category=949323
        //위치정보 권한 요청
        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY //사용자의 정확한 위치정보에 접근한 권한을 요청한다.
        }

        //레이아웃의 슬라이딩 패널을 찾아서 리스너를 달아준다. 이제 터치나 슬라이드와 같은 행동에 반응한다.
        slidePanel = binding.mainFrame
        slidePanel.addPanelSlideListener(PanelEventListener())

        //마커 리스너를 달아준다. 마커를 누르거나 드래그 하는 행동에 반응한다.
        eventListener = MarkerEventListener(this, slidePanel)

        //커스텀 말풍선 등록. 말풍선의 리스너도 달아준다.
        mapView!!.setCalloutBalloonAdapter(CustomBallonAdapter(layoutInflater))
        mapView!!.setPOIItemEventListener(eventListener)  // 마커 클릭 이벤트 리스너 등록

        //병원이 아닐 경우에는 병원과 관련된 안내사항을 표시하지 않는다.
        if (mapKeyword == "Pharm" || mapKeyword == "Egy") {
            binding.infoConstraint.visibility = View.GONE
        }

        val curTime = System.currentTimeMillis() //현재 시간을 ms로 반환
        val timeFormat = SimpleDateFormat("HHmm") // 시분 형식 ex) 오후 6시 30분 = 1830
        val eFormat = SimpleDateFormat("E", Locale("ko", "KR")) //요일을 한국어로 ex) 화요일이면 "화"
        currentTime = timeFormat.format(curTime).toInt() //위 형식에 맞게 현재 시각 변환
        currentDay = eFormat.format(curTime) //위 형식에 맞게 요일 변환

        dialog!!.show() //로딩창을 표시
    }



    override fun onStart() {
        super.onStart()
        binding.mapText.text = "가까운 $mapKeyword 입니다." //검색한 키워드를 출력한다.

        if (mapKeyword != "Pharm" && mapKeyword != "Egy") { //병원일 경우 창을 열자마자 안내창 출력한다.
            AlertDialog.Builder(this)
                .setTitle("주의사항")
                .setMessage("반경 2km 이내 진료기관 검색 결과입니다. 실제 접수 마감 시간과는 차이가 있을 수 있으니 방문 전에 해당 기관에 문의하시기 바랍니다.")
                .create()
                .show()
        }

        if (checkLocationService()) { // GPS가 켜져있을 경우
            permissionCheck() //위치 권한 체크

        } else { // GPS가 꺼져있을 경우
            Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
        }

        binding.causion.setOnClickListener { //주의사항 버튼을 누르면 알림창이 뜨도록 리스너를 설정한다.
            AlertDialog.Builder(this)
                .setTitle("주의사항")
                .setMessage("반경 2km 이내 진료기관 검색 결과입니다. 실제 접수 마감 시간과는 차이가 있을 수 있으니 방문 전에 해당 기관에 문의하시기 바랍니다.")
                .create()
                .show()
        }

        binding.gotoMain.setOnClickListener { //메인 메뉴로
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }

    //뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
        startActivity(intent) //인텐트 이동
        finish() //현재 액티비티 종료
    }

    // GPS가 켜져있는지 확인
    private fun checkLocationService(): Boolean {
        Log.d("MAP", "checkLocationService: GPS 켜져 있는지 확인")
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // 위치 권한 확인
    private fun permissionCheck() {
        Log.d("MAP", "permissionCheck: 권한 체크")
        //권한 설정이 처음인지 여부 확인
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 -> 다시 한 번 물어본다
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_FINE_LOCATION)
                    startLocationUpdates() //사용자 현재 위치 추적
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            }
            else {
                if (isFirstCheck) { //최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_FINE_LOCATION)
                }
                else { //다시 묻지 않음 클릭 (앱 정보 화면으로 이동) -> 권한을 거절했기 때문에 설정 창에서 허용하도록 이동 시켜줌
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { dialog, which ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { dialog, which ->
                        //취소 시 아무 행동도 하지 않음
                    }
                    builder.show()
                }
            }
        }
        else { // 권한이 있는 상태
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) //안드로이드 SDK 31 미만이면
                startTracking() //카카오맵 위치 트래킹 시작. 31 이상은 뒤에서 다른 방법으로 처리한다.
            startLocationUpdates() //사용자 현재 위치 추적
        }
    }

    //실시간으로 사용자의 위치를 Update한다.
    private fun startLocationUpdates() {
        Log.d("MAP", "startLocationUpdate: 기기 위치 업데이트")

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

    //마지막 위치를 전달 받아 검색을 시작한다.
    private fun getLastLocation(location: Location) { //주소 값 반환
        Log.d("MAP", "getLastLocation: 기기 위치를 x, y에")

        x = location.longitude.toString() //현재 x좌표
        y = location.latitude.toString() //현재 y좌표


        val geoCoder = Geocoder(context()) //x, y 좌표를 주소로 변환한다. (응급기관 api에서 "OO시" "OO구" 형식을 요구하기 때문)
        currAddress = geoCoder.getFromLocation(location.latitude, location.longitude, 3)
        STAGE1 = currAddress[0].adminArea //OO시
        STAGE2 = currAddress[0].subLocality //OO구

        //안드로이드 SDK 31 이상에서는 kakao map의 실시간 위치 tracking이 대응하지 않기 때문에 다른 방법으로 현재 위치를 표시한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startTrackingOn31()
        }

        //현재 좌표를 설정한 뒤 2초 후 키워드 검색 결과를 받아옴
        Handler().postDelayed({
            when (mapKeyword) { //맵 키워드에 따라
                "Pharm" -> searchKeyword("약국", x!!, y!!) //약국 -> Kakao 로컬에서 약국을 검색한다.
                "Egy" -> getEmergency()// 응급실 -> 응급의료기관 api에 검색한다.
                else -> { //병원
                    if (mapKeyword.contains(',')) //진료과 여러개가 추천된 경우, 콤마 단위로 잘라서 Kakao 로컬에서 모두 검색한다.
                    {
                        searchKeyword(splitMapKeyword(mapKeyword), x!!, y!!)
                    }
                    else //진료과 한개가 추천된 경우, Kakao 로컬에서 진료과를 검색한다.
                        searchKeyword(mapKeyword, x!!, y!!)
                }
            }
        }, 2000)
    }


    //SDK 31 이상인 경우, 수동으로 사용자의 위치를 표시한다.
    private fun startTrackingOn31() {
        Log.d("MAP", "startTrackingOn31: SDK 31 이상")

        //현재 위치 좌표를 지도 위 좌표로 변환한다.
        mapView!!.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(y!!.toDouble(), x!!.toDouble()), true)

        //변환한 지도 좌표에 현 위치 마커를 생성한다.
        val marker = MapPOIItem()
        marker.apply{
            itemName = "현위치"
            mapPoint = MapPoint.mapPointWithGeoCoord(y!!.toDouble(), x!!.toDouble())
            marker.markerType = MapPOIItem.MarkerType.RedPin
            marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        }
        mapView!!.addPOIItem(marker) //맵 뷰에 마커를 추가한다.
    }

    // 권한 요청 후 안내 메세지 출력
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d("MAP", "onRequestPermissionsResult: 위치 권한 요청 후 결과에 따라 행동")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
                startTracking()
                startLocationUpdates() //사용자 현재 위치 추적
            } else {
                // 권한 요청 후 거절됨 (다시 요청 or 토스트)
                Toast.makeText(this, "위치 권한이 거절되었습니다", Toast.LENGTH_SHORT).show()
                permissionCheck()
            }
        }
    }



    // 위치추적 시작
    private fun startTracking() {
        Log.d("MAP", "startTracking: 트래킹중")
        mapView!!.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
    }



    //진료과가 여러개인 경우, 콤마 단위로 잘라서 List로 반환한다.
    private fun splitMapKeyword(keyword: String) : List<String> = keyword.split(',')


    //단일 키워드 검색
    private fun searchKeyword(keyword: String, x:String, y:String, radius: Int=2000) { //기본값으로 2km 이내 검색 결과를 찾도록 한다.
        Log.d("MAP", "searchKeyword: 검색")

        val retrofitMap = Retrofit.Builder()   // Retrofit 생성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofitMap.create(KakaoAPI::class.java)   //통신 인터페이스를 객체로 생성



        val call = api.getSearchKeyword(API_KEY, keyword, x, y, radius)   //검색 조건 입력 - api Key, 검색어, x좌표, y좌표, 반경

        // 카카오 API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                if (response.isSuccessful) { //통신이 성공적이면
                    if (response.body()!!.meta.total_count > 0) { //검색결과가 하나라도 있으면
                        if (mapKeyword == "Pharm") //약국이면
                            for (ele in response.body()!!.documents)
                                drawMapMarker(ele, null) //바로 마커를 그린다.

                        else getHospital(response.body()!!.documents) //병원이면 의료기관 api를 거쳐서 마커를 그린다 (진료 시간 확인 위해)
                    }
                    else //검색 결과가 없을 경우
                        Toast.makeText(context(), "반경 2km 이내 검색결과가 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
        dialog!!.dismiss() //로딩 창 삭제
    }

    //복수 키워드 검색
    private fun searchKeyword(keywords: List<String>, x:String, y:String, radius:Int = 2000) {
        Log.d("MAP", "searchKeyword: 검색")
        val retrofitMap = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofitMap.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성

        var total_cnt = 0
        for (keyword in keywords) { //키워드 하나씩 검색
            val call = api.getSearchKeyword(API_KEY, keyword, x, y, radius)   // 검색 조건 입력

            // API 서버에 요청
            call.enqueue(object: Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    total_cnt += response.body()!!.meta.total_count //총 검색 결과 수를 구한다.
                    if (response.body()!!.meta.total_count > 0) //검색결과가 있으면
                        getHospital(response.body()!!.documents) //병원이면 의료기관 api를 거쳐서 마커를 그린다 (진료 시간 확인 위해)
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    // 통신 실패
                    Log.w("MainActivity", "통신 실패: ${t.message}")
                }
            })
        }

        //모든 키워드를 검색하고도 결과가 하나도 없으면 안내문구 출력
        Handler().postDelayed({
            if (total_cnt <= 0) {
                Toast.makeText(context(), "반경 2km 이내 검색결과가 없습니다.", Toast.LENGTH_LONG).show()
                dialog!!.dismiss()
            }
        }, 5000)


    }

    // 구내 응급실을 조회한다.
    private fun getEmergency() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // http요청이 많아서 timeout 시간을 더 길게 설정했다.
        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()


        // xml 파서를 생성
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofit_e = Retrofit.Builder()
            .baseUrl(E_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(TikXmlConverterFactory.create(parser)) //xml 파서를 retrofit에 달아준다
            .build()

        //응급기관 api를 생성
        val emergencyAPI = retrofit_e.create(EmergencyAPI::class.java)

        //OO시, OO구, API KEY를 입력하여 구 내의 응급기관를 조회한다.
        emergencyAPI!!.getEmergencybyAdd(STAGE1, STAGE2, EKEY).enqueue(object : Callback<Emergency1> {
            override fun onResponse(call: Call<Emergency1>, response: Response<Emergency1>) {
                if (response.isSuccessful) { //통신이 성공적이면
                    for (egy in response.body()!!.body.items.item) { //검색 결과 중 응급실의 기관 id로 상세 정보를 조회한다
                        emergencyAPI!!.getEmergencybyID(egy.hpid!!, EKEY).enqueue(object : Callback<Emergency2> {
                            override fun onResponse(
                                call: Call<Emergency2>,
                                response: Response<Emergency2>
                            ) {
                                if (response.isSuccessful) { //통신이 성공적이면 지도에 마커를 그린다.
                                    drawMapMarker(egy, response.body()?.body?.items?.item?.get(0))
                                }
                                dialog?.dismiss() //로딩창이 있는 경우 없앤다.
                            }

                            override fun onFailure(call: Call<Emergency2>, t: Throwable) {
                                dialog?.dismiss() //로딩창이 있는 경우 없앤다.
                            }
                        })
                    }
                }

            }
            override fun onFailure(call: Call<Emergency1>, t: Throwable) {
                println(t.message)
                Toast.makeText(context(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                dialog?.dismiss()
            }
        })
    }


    //카카오 로컬 검색 결과를 바탕으로 병원 정보를 조회한다.
    fun getHospital(searchResult : List<Place>) {
        Log.d("MAP", "getHospital: 병원 정보 api")
        // timeout setting 해주기
        val okHttpClient = OkHttpClient().newBuilder()
            .followRedirects(FALSE)
            .followSslRedirects(FALSE)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()


        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val retrofit_h = Retrofit.Builder()
            .baseUrl(H_BASE_URL) //병원 api url
            .client(okHttpClient)
            .addConverterFactory(TikXmlConverterFactory.create(parser)) //xml 파서를 단다
            .build()

        val hospitalAPI = retrofit_h.create(HospitalAPI::class.java)


        var cnt = 0
        for (place in searchResult) {
            cnt++
            if (cnt > 20) break //검색 결과가 20개를 넘을 경우, 과부하를 막기 위해 20개까지만 표시한다.
            //병원 이름으로 상세 정보를 검색한다. 전국에 같은 이름의 병원이 있을수도 있기 때문에 현재 위치한 '구'와 일치되는 검색 결과만 가져온다
            hospitalAPI!!.getHospitalInfobyName(STAGE2, place.place_name,1, 10, HKEY).enqueue(object : Callback<Hospital>{
                override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                    if (response.isSuccessful) { //통신이 성공적이면
                        drawMapMarker(place, response.body()!!.body.items.item.get(0)) //지도에 마커를 그린다
                    }
                    dialog?.dismiss() //로딩창이 있는 경우 없앤다.
                }
                override fun onFailure(call: Call<Hospital>, t: Throwable) {
                    println(t.message)
                    dialog?.dismiss() //로딩창이 있는 경우 없앤다.
                }
            })
        }

    }


    //약국과 병원의 마커를 그리는 함수. hitem은 병원 정보 클래스
    private fun drawMapMarker(place: Place, hitem: HItem?) {
        Log.d("MAP", "drawMapMarker: 마커 찍기")
            val marker = MapPOIItem() //마커 생성

            if (mapKeyword.equals("Pharm")) { //약국 -> 약국 아이콘 이미지로 마커를 만든다.
                marker.apply {
                    itemName = place.place_name //장소명
                    mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble()) //지도상 좌표
                    markerType = MapPOIItem.MarkerType.CustomImage //마커 이미지 타입 -> 커스텀
                    customImageResourceId = R.drawable.markerpharm //약국 마커 이미지 지정
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양
                    customSelectedImageResourceId = R.drawable.markerpharmsele   // 클릭 시 커스텀 마커 이미지
                    isCustomImageAutoscale = false  // 커스텀 마커 이미지 크기 자동 조정 막기
                    setCustomImageAnchor(0.5f, 1.0f)  // 마커 이미지 기준점
                    userObject = BallonInfo("Pharm", place.id ,place.phone, place.road_address_name, null) //커스텀 오브젝트 붙여주기
                }
            }
            else { //병원
                if (hitem != null && isOperate(hitem)) { //병원이 현재 진료중이면
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.markerhos //하늘색 마커로 지정한다.
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        customSelectedImageResourceId = R.drawable.markerhosele
                        isCustomImageAutoscale = false
                        setCustomImageAnchor(0.5f, 1.0f)
                        userObject = BallonInfo("Hos",place.id ,place.phone, place.road_address_name, null)
                    }
                }
                else { //병원이 진료 종료했으면
                    marker.apply {
                        itemName = place.place_name
                        mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                        markerType = MapPOIItem.MarkerType.CustomImage
                        customImageResourceId = R.drawable.disablemarker //회색 마커로 지정한다.
                        selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                        customSelectedImageResourceId = R.drawable.markerhosele
                        isCustomImageAutoscale = false
                        setCustomImageAnchor(0.5f, 1.0f)
                        userObject = BallonInfo("Hos", place.id ,place.phone, place.road_address_name, null)
                    }
                }
            }
            mapView!!.addPOIItem(marker) //맵뷰에 마커를 추가한다
    }

    //응급실의 마커를 그리는 함수. eitem1은 첫번째 지역 내 응급실 조회 결과, eitem2는 응급실 하나의 상세 정보
    private fun drawMapMarker(eItem1: EItem1, eItem2: EItem2?) {
        Log.d("MAP", "drawMapMarker: 마커 찍기")
        val marker = MapPOIItem() //마커 생성

        marker.apply {
            itemName = eItem1.dutyName //기관명
            mapPoint = MapPoint.mapPointWithGeoCoord(eItem1.wgs84Lat!!.toDouble(), eItem1.wgs84Lon!!.toDouble()) //지도 내 좌표
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.markerhos
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customSelectedImageResourceId = R.drawable.markerhosele
            isCustomImageAutoscale = false
            setCustomImageAnchor(0.5f, 1.0f)
            userObject = BallonInfo("Egy", eItem1.hpid!!, eItem1.dutyTel1!!, eItem1.dutyAddr!!, eItem2) //커스텀 오브젝트 붙여주기
        }
        mapView!!.addPOIItem(marker) //맵뷰에 마커를 추가한다
    }


    //병원의 진료 여부를 반환하는 함수
    private fun isOperate(hitem: HItem) : Boolean {
        when(currentDay) { //요일에 따라 현재 시간이 진료 시작 시간과 종료 시간 사이에 있으면 true, 없으면 false 반환
            "월" -> return currentTime >= hitem.dutyTime1s!! && currentTime <= hitem.dutyTime1c!!
            "화" -> return currentTime >= hitem.dutyTime2s!! && currentTime <= hitem.dutyTime2c!!
            "수" -> return currentTime >= hitem.dutyTime3s!! && currentTime <= hitem.dutyTime3c!!
            "목" -> return currentTime >= hitem.dutyTime4s!! && currentTime <= hitem.dutyTime4c!!
            "금" -> return currentTime >= hitem.dutyTime5s!! && currentTime <= hitem.dutyTime5c!!
            "토" -> return currentTime >= hitem.dutyTime6s!! && currentTime <= hitem.dutyTime6c!!
            "일" -> return currentTime >= hitem.dutyTime7s!! && currentTime <= hitem.dutyTime7c!!
            else -> return false
        }
    }

    //마커 클릭 시 출력되는 말풍선에 관련된 작업을 하는 어댑터
    class CustomBallonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.customballon, null) //말풍선 레이아웃을 연결
        val name: TextView = mCalloutBalloon.findViewById(R.id.placename) //말풍선 속 이름 text
        val phone: TextView = mCalloutBalloon.findViewById(R.id.placephone) //말풍선 속 전화번호 text
        val address: TextView = mCalloutBalloon.findViewById(R.id.placeaddress) //말풍선 속 장소 text

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            //마커 클릭시 나오는 말풍선
            if (poiItem?.userObject != null) { //커스텀 object가 있으면
                val ballonInfo :BallonInfo = poiItem?.userObject as BallonInfo //말풍선 클래스를 불러와서 UI를 갱신한다.
                name.text = poiItem?.itemName
                phone.text = ballonInfo.phone
                address.text = ballonInfo.address
            }
            else { //혹시라도 커스텀 object가 존재하지 않으면
                name.text = poiItem?.itemName //이름만 출력하고 나머지 정보는 공란으로 비워둔다
                phone.text = ""
                address.text = ""
            }
            return mCalloutBalloon //말풍선을 리턴한다.
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            //말풍선 클릭 시 행동은 마커 이벤트에서 처리한다.
            //마커 클릭 시 중복 실행 방지
            return mCalloutBalloon
        }
    }


    class MarkerEventListener(val context: Context, val slidePanel: SlidingUpPanelLayout): MapView.POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
        }
        //말충선을 눌렀을 때의 행동
        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {
            if (poiItem?.userObject != null) { //커스텀 object가 있으면
                val ballonInfo :BallonInfo = poiItem?.userObject as BallonInfo //말풍선 클래스를 가져온다

                if (ballonInfo.type != "Egy") { //병원, 약국의 경우
                    val urladdress = "https://place.map.kakao.com/" + ballonInfo.id //카카오의 기관 id를 이용해 카카오 정보 페이지로 이동한다.
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urladdress)) //인터넷 브라우저를 열어 해당 url로 이동시킨다.
                    startActivity(context, intent, null)
                }
                else { //응급실 정보가 표시되는 슬라이딩 패널 레이아웃에 데이터를 연결한다.
                    slidePanel.findViewById<TextView>(R.id.dutyName).text = ballonInfo.eItem2!!.dutyName
                    slidePanel.findViewById<TextView>(R.id.dutyAddr).text = ballonInfo.eItem2!!.dutyAddr
                    slidePanel.findViewById<TextView>(R.id.dutyTel1).text = ballonInfo.eItem2!!.dutyTel1
                    slidePanel.findViewById<TextView>(R.id.dutyTel3).text = ballonInfo.eItem2!!.dutyTel3
                    slidePanel.findViewById<TextView>(R.id.dgidIdName).text = ballonInfo.eItem2!!.dgidIdName!!.replace(",", ", ")
                    slidePanel.findViewById<TextView>(R.id.hpbdn).text = ballonInfo.eItem2!!.hpbdn
                    slidePanel.findViewById<TextView>(R.id.hpccuyn).text = ballonInfo.eItem2!!.hpccuyn
                    slidePanel.findViewById<TextView>(R.id.hpcuyn).text = ballonInfo.eItem2!!.hpcuyn
                    slidePanel.findViewById<TextView>(R.id.hperyn).text = ballonInfo.eItem2!!.hperyn
                    slidePanel.findViewById<TextView>(R.id.hpgryn).text = ballonInfo.eItem2!!.hpgryn
                    slidePanel.findViewById<TextView>(R.id.hpicuyn).text = ballonInfo.eItem2!!.hpicuyn
                    slidePanel.findViewById<TextView>(R.id.hpnicuyn).text = ballonInfo.eItem2!!.hpnicuyn
                    slidePanel.findViewById<TextView>(R.id.hpopyn).text = ballonInfo.eItem2!!.hpopyn
                    slidePanel.findViewById<TextView>(R.id.MKlist).text = combineMKList(ballonInfo.eItem2!!)

                    //입원실 가용 여부
                    if (ballonInfo.eItem2!!.dutyHayn == 1) slidePanel.findViewById<TextView>(R.id.dutyHayn).text = "가능"
                    else slidePanel.findViewById<TextView>(R.id.dutyHayn).text = "불가"

                    //응급실 가용 여부
                    if (ballonInfo.eItem2!!.dutyEryn == 1) slidePanel.findViewById<TextView>(R.id.dutyEryn).text = "가능"
                    else slidePanel.findViewById<TextView>(R.id.dutyEryn).text = "불가"

                    //패널이 접힌 상태면 열고, 열린 상태면 접는다.
                    if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED)
                        slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    else if (slidePanel.panelState == SlidingUpPanelLayout.PanelState.EXPANDED)
                        slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

                    //전화 아이콘을 누르면 전화 앱을 열어 대표전화 번호를 입력해준다.
                    slidePanel.findViewById<ImageView>(R.id.callbtn).setOnClickListener {
                        val phonenum = Uri.parse("tel:${ballonInfo.eItem2!!.dutyTel1}")
                        var intent = Intent(Intent.ACTION_VIEW, phonenum)
                        startActivity(context, intent, null)
                    }
                }
            }
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
        }

        //진료 가능 항목을 String으로 합성한다.
        private fun combineMKList(eItem2: EItem2) : String {
            var result = ""

            if (eItem2.MKioskTy25 == "Y") result += "응급실, "
            if (eItem2.MKioskTy1 == "Y") result += "뇌출혈수술, "
            if (eItem2.MKioskTy2 == "Y") result += "뇌경색의재관류, "
            if (eItem2.MKioskTy3 == "Y") result += "심근경색의재관류, "
            if (eItem2.MKioskTy4 == "Y") result += "복부손상의수술, "
            if (eItem2.MKioskTy5 == "Y") result += "사지접합의수술, "
            if (eItem2.MKioskTy6 == "Y") result += "응급내시경, "
            if (eItem2.MKioskTy7 == "Y") result += "응급투석, "
            if (eItem2.MKioskTy8 == "Y") result += "조산산모, "
            if (eItem2.MKioskTy9 == "Y") result += "정신질환자, "
            if (eItem2.MKioskTy10 == "Y") result += "신생아, "
            if (eItem2.MKioskTy11 == "Y") result += "중증화상"

            return result
        }

    }

    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            //패널 슬라이드 중
        }

        override fun onPanelStateChanged(
            panel: View?,
            previousState: SlidingUpPanelLayout.PanelState?,
            newState: SlidingUpPanelLayout.PanelState?
        ) {
            //패널 상태가 변했을 때
        }
    }

}
