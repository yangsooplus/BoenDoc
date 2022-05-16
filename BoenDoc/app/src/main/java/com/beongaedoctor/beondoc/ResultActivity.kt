package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beongaedoctor.beondoc.databinding.ActivityResultBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ResultActivity : AppCompatActivity() {

    //다른 결과 생길 시 사용.
    var otherResultList = arrayListOf<OtherResult>(
        OtherResult("1번 질병", 0, "응급질병의 경우"),
        OtherResult("2번 질병", 1, "중증질병의 경우"),
        OtherResult("3번 질병", 2, "경증질병의 경우")
    )

    // 전역 변수로 바인딩 객체 선언
    private var RABinding: ActivityResultBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = RABinding!!

    private lateinit var diagnosisService : DiagnosisService
    private lateinit var retrofit: Retrofit

    lateinit var memberInfo : Member

    private lateinit var predDiseaseInfo : DiagnosisRecord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RABinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diagnosisService = retrofit.create(DiagnosisService::class.java)


        val pred_disease = intent.getStringExtra("diseaseName")
        println(pred_disease)

        //기기에 저장된 유저 정보
        memberInfo = App.prefs.getMember("memberInfo", "")

        if (pred_disease != null)
            getDiseaseInfo(DN(pred_disease))


        //다른 결과는 리스트로.
        binding.otherResultList.adapter = OtherResultAdapter(this, otherResultList)


        //약국 가는 버튼
        binding.gotoPharmacy.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("mapKeyword", "약국")
            startActivity(mapIntent)
        }

        //병원 가는 버튼. 검색할 진료과를 putExtra로 보내야 함.
        binding.gotoHospital.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("mapKeyword", predDiseaseInfo.department)
            startActivity(mapIntent)
        }
    }

    //질병id -> 질병 정보
    private fun getDiseaseInfo0(dnid : DNID) {
        diagnosisService.getDiseasebyDNID(dnid.id).enqueue(object : Callback<DiagnosisRecord> {
            override fun onResponse(
                call: Call<DiagnosisRecord>,
                response: Response<DiagnosisRecord>
            ) {
                if (response.isSuccessful) {
                    println(response.body())
                    setMainDiseaseInfo(response.body()) //받아온 정보를 메인 질병에 셋팅
                }
            }

            override fun onFailure(call: Call<DiagnosisRecord>, t: Throwable) {
                println(t.message)
            }

        })
    }

    //질병id -> 질병 정보
    private fun getDiseaseInfo(dn : DN) {
        diagnosisService.getDiseasebyString(memberInfo.id, dn).enqueue(object : Callback<DiagnosisRecord> {
            override fun onResponse(
                call: Call<DiagnosisRecord>,
                response: Response<DiagnosisRecord>
            ) {
                if (response.isSuccessful) {
                    println(response.body())
                    predDiseaseInfo = response.body()!!
                    setMainDiseaseInfo(response.body()) //받아온 정보를 메인 질병에 셋팅
                }
            }

            override fun onFailure(call: Call<DiagnosisRecord>, t: Throwable) {
                println(t.message)
            }
        })
    }

    //받아온 정보를 메인 질병에 셋팅
    private fun setMainDiseaseInfo(DR : DiagnosisRecord?) {
        binding.maindiseaseName.text = DR?.name
        binding.mainexplanation.text = DR?.info

        when (DR?.level) { //중증도에 따라 textView 내용과 배경 바꾸기
            0 -> {
                binding.mainseverity.text = "응급"
                binding.mainseverity.setBackgroundResource(R.drawable.rectemergency)
            }
            1 -> {
                binding.mainseverity.text = "중증"
                binding.mainseverity.setBackgroundResource(R.drawable.rectserious)
            }
            2 -> {
                binding.mainseverity.text = "경증"
                binding.mainseverity.setBackgroundResource(R.drawable.rectlight)
            }
        }

    }
}