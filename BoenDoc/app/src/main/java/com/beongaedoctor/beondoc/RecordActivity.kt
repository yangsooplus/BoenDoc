package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.databinding.ActivityRecordBinding

class RecordActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var rbinding: ActivityRecordBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = rbinding!!

    val RecordList = arrayListOf(
        Record("2022-04-21", "예시질병1"),
        Record("2022-04-22", "예시질병2"),
        Record("2022-04-23", "예시질병3")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rbinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mypageRecyclerView.adapter = RecordAdapter(RecordList)
    }


}