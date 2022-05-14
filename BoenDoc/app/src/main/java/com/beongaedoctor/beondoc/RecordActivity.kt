package com.beongaedoctor.beondoc

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.App.Companion.context
import com.beongaedoctor.beondoc.databinding.ActivityRecordBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class RecordActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var rbinding: ActivityRecordBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = rbinding!!


    var diagnosisList : DiagnosisList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rbinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //기기에 저장된 유저 정보
        val memberInfo = App.prefs.getMember("memberInfo", "")


        //서버 연결
        val retrofit = RetrofitClass.getInstance()
        val diagnosisRecordService = retrofit.create(DiagnosisRecordService::class.java)
        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)

        //사용자 id -> 해당 사용자의 진단 내역 리스트
        diagnosisRecordService!!.getDiagnosisRecord(memberInfo.id).enqueue(object :
            Callback<DiagnosisList>{
            override fun onResponse(call: Call<DiagnosisList>, response: Response<DiagnosisList>) {
                if (response.isSuccessful) {
                    diagnosisList = response?.body()
                    println(response.body())

                    //받아온 내역 리스트를 RecyclerView에 연결해서 표시
                    binding.mypageRecyclerView.adapter = DiagnosisAdapter(diagnosisList!!)
                }
            }

            override fun onFailure(call: Call<DiagnosisList>, t: Throwable) {
                println(t.message)
            }

        })
    }
}