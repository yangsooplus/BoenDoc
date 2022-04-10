package com.beongaedoctor.beondoc


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.beongaedoctor.beondoc.databinding.ActivityMapBinding
import com.google.android.gms.location.*
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



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
    }

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    private var x : String? = null //현재 위치 x좌표.
    private var y : String? = null //현재 위치 y좌표. null인 경우는 못 불러온 경우임.
    private val REQUEST_PERMISSION_LOCATION = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapbinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = MapView(this)
        binding.mapview.addView(mapView)


        //https://fre2-dom.tistory.com/134?category=949323
        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        // 위치추적 버튼
        binding.getCurLocation.setOnClickListener {
            if (checkLocationService()) {
                // GPS가 켜져있을 경우
                permissionCheck()
                startLocationUpdates()

            } else {
                // GPS가 꺼져있을 경우
                Toast.makeText(this, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }



        binding.gotoMain.setOnClickListener {
            searchKeyword("이비인후과", x!!, y!!)
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
    }



    // 위치 권한 확인
    private fun permissionCheck() {
        val preference = getPreferences(MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 거절 (다시 한 번 물어봄)
                val builder = AlertDialog.Builder(this)
                builder.setMessage("현재 위치를 확인하시려면 위치 권한을 허용해주세요.")
                builder.setPositiveButton("확인") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                builder.setNegativeButton("취소") { dialog, which ->

                }
                builder.show()
            } else {
                if (isFirstCheck) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
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
        } else {
            // 권한이 있는 상태
            startTracking()
        }
    }

    // 권한 요청 후 행동
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 후 승인됨 (추적 시작)
                Toast.makeText(this, "위치 권한이 승인되었습니다", Toast.LENGTH_SHORT).show()
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
    private fun searchKeyword(keyword: String, x:String, y:String, radius:Int = 200) {
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
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }



}
