package com.beongaedoctor.beondoc

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        //기기에 저장된 유저 정보 불러오기 - 유저 id를 사용하기 위함
        val memberInfo = App.prefs.getMember("memberInfo", "")

        val retrofit = RetrofitClass.getInstance() //서버 연결
        val diagnosisRecordService = retrofit.create(DiagnosisService::class.java) //진단 기록 api

        val diaList = arrayListOf<List<Diagnosis>>() //진단 리스트
        val dAdpater = DiagnosisAdapter(diaList) //진단 기록 RecyclerView 어댑터
        binding.mypageRecyclerView.layoutManager =
            LinearLayoutManager(this) //수직 드래그를 위해 LinearLayoutManaer 연결
        binding.mypageRecyclerView.adapter = dAdpater //어댑터 연결


        //사용자 id -> 해당 사용자의 진단 내역 리스트
        diagnosisRecordService!!.getDiagnosisRecord(memberInfo.id).enqueue(object :
            Callback<DiagnosisList> {
            override fun onResponse(call: Call<DiagnosisList>, response: Response<DiagnosisList>) {
                if (response.isSuccessful) { //통신이 성공적이면
                    for (dia in response.body()!!.diagnosisList) {
                        diaList.add(dia) //진단 기록 리스트에 저장
                    }
                    diaList.reverse() //최근 진단이 상위에 위치하도록 역순으로 정렬
                    dAdpater.notifyDataSetChanged() //어댑터에 연결된 리스트인 diaList가 변경됨을 알림 -> 화면 갱신
                }
            }

            override fun onFailure(call: Call<DiagnosisList>, t: Throwable) {
                Toast.makeText(context(), "오류 발생", Toast.LENGTH_SHORT).show()
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