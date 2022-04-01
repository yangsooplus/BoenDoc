package com.beongaedoctor.beondoc


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.beongaedoctor.beondoc.databinding.ActivityMapBinding
import net.daum.mf.map.api.MapView


class MapActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mapbinding: ActivityMapBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mapbinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapbinding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapView = MapView(this)
        binding.mapview.addView(mapView)

    }
}