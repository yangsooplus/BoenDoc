package com.beongaedoctor.beondoc

import android.content.Intent
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rbinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //기기에 저장된 유저 정보
        val memberInfo = App.prefs.getMember("memberInfo", "")


        //서버 연결
        val retrofit = RetrofitClass.getInstance()
        val diagnosisRecordService = retrofit.create(DiagnosisService::class.java)

        var diaList = arrayListOf<List<Diagnosis>>()
        var dAdpater = DiagnosisAdapter(diaList)
        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mypageRecyclerView.adapter = dAdpater



        //사용자 id -> 해당 사용자의 진단 내역 리스트
        diagnosisRecordService!!.getDiagnosisRecord(memberInfo.id).enqueue(object :
            Callback<DiagnosisList>{
            override fun onResponse(call: Call<DiagnosisList>, response: Response<DiagnosisList>) {
                if (response.isSuccessful) {

                    for (dia in response.body()!!.diagnosisList) {
                        diaList.add(dia)
                    }

                    diaList.reverse()
                    dAdpater.notifyDataSetChanged()

                    //받아온 내역 리스트를 RecyclerView에 연결해서 표시
                    //binding.mypageRecyclerView.adapter = DiagnosisAdapter(diagnosisList)
                }
            }

            override fun onFailure(call: Call<DiagnosisList>, t: Throwable) {
                println(t.message)
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainMypageActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
        startActivity(intent) //인텐트 이동
        finish() //현재 액티비티 종료
    }
}