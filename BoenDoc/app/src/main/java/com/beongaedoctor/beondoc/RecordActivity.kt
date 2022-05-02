package com.beongaedoctor.beondoc

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.databinding.ActivityRecordBinding
import java.time.LocalDateTime

class RecordActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var rbinding: ActivityRecordBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = rbinding!!

    val disease = Disease(0, "이름","설명", 0, "정형외과")
    var DDlist : List<DiagnosisDisease> = listOf(DiagnosisDisease(0, disease))

    @RequiresApi(Build.VERSION_CODES.O)
    val DiagnosisList = arrayListOf(
        Diagnosis(0, Member(), DDlist, LocalDateTime.now(), 0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rbinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mypageRecyclerView.adapter = DiagnosisAdapter(DiagnosisList)
    }


}