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

    //val disease = Disease(0, "이름","설명", 0, "정형외과")
    //var DDlist : List<DiagnosisDisease> = listOf(DiagnosisDisease(0, disease))


    var diagnosisList : DiagnosisList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rbinding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터 저장
        val sp = getSharedPreferences("shared",MODE_PRIVATE)
        val gson = GsonBuilder().create()
        //기기에 저장된 유저 정보
        val gsonMemberInfo = sp!!.getString("memberInfo","")
        val memberInfo = gson!!.fromJson(gsonMemberInfo, Member::class.java)


        //서버 연결
        val retrofit = RetrofitClass.getInstance()
        val diagnosisRecordService = retrofit.create(DiagnosisRecordService::class.java)
        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)

        diagnosisRecordService!!.getDiagnosisRecord(memberInfo.id).enqueue(object :
            Callback<DiagnosisList>{
            override fun onResponse(call: Call<DiagnosisList>, response: Response<DiagnosisList>) {
                if (response.isSuccessful) {
                    diagnosisList = response?.body()
                    println(response.body())


                    binding.mypageRecyclerView.adapter = DiagnosisAdapter(diagnosisList!!)
                }
            }

            override fun onFailure(call: Call<DiagnosisList>, t: Throwable) {
                println(t.message)
            }

        })


    }


}