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

    private var sp : SharedPreferences? = null
    private var gson : Gson? = null

    private var memberInfo : Member? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RABinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diagnosisService = retrofit.create(DiagnosisService::class.java)


        //데이터 저장
        sp = getSharedPreferences("shared",MODE_PRIVATE)
        gson = GsonBuilder().create()

        //기기에 저장된 유저 정보
        val gsonMemberInfo = sp!!.getString("memberInfo","")
        memberInfo = gson!!.fromJson(gsonMemberInfo, Member::class.java)

        diagnosisService.searchDiseasebyString(memberInfo!!.id,DN("탈모")).enqueue(object : Callback<DNID> {
            override fun onResponse(
                call: Call<DNID>,
                response: Response<DNID>
            ) {
                println(response.errorBody()?.string())
                if (response.isSuccessful) {

                    println("받은 아이디: " + response.body())
                    if ( response.body() != null)
                        getDiseaseInfo(response.body()!!)
                }
            }

            override fun onFailure(call: Call<DNID>, t: Throwable) {
                println("실패")
            }

        })



        binding.otherResultList.adapter = OtherResultAdapter(this, otherResultList)

        binding.gotoPharmacy.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("mapKeyword", "약국")
            startActivity(mapIntent)
        }


        binding.gotoHospital.setOnClickListener {
            val mapIntent = Intent(this, MapActivity::class.java)
            mapIntent.putExtra("mapKeyword", "정형외과")
            startActivity(mapIntent)
        }


    }

    private fun getDiseaseInfo(dnid : DNID) {
        diagnosisService.getDiseasebyDNID(dnid.id).enqueue(object : Callback<DiagnosisRecord> {
            override fun onResponse(
                call: Call<DiagnosisRecord>,
                response: Response<DiagnosisRecord>
            ) {
                if (response.isSuccessful) {
                    println(response.body())
                    setMainDiseaseInfo(response.body())
                }
            }

            override fun onFailure(call: Call<DiagnosisRecord>, t: Throwable) {
                println(t.message)
            }

        })
    }

    private fun setMainDiseaseInfo(DR : DiagnosisRecord?) {
        binding.maindiseaseName.text = DR?.name
        binding.mainexplanation.text = DR?.info

        when (DR?.level) {
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