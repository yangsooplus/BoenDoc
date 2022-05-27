package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beongaedoctor.beondoc.databinding.ActivityDinfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class DInfoActivity : AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private var DIBinding: ActivityDinfoBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = DIBinding!!

    private lateinit var retrofit: Retrofit
    private lateinit var diseaseService : DiseaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DIBinding = ActivityDinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diseaseService = retrofit.create(DiseaseService::class.java)

        val dn = intent.getStringExtra("diseaseName")
        val did = intent.getLongExtra("diseaseID", -1L)

        if (did == -1L)
            getDiseaseInfo(dn!!)
        else
            getDiseaseInfo(did)
    }

    private fun getDiseaseInfo(searchFlag : String) {
        diseaseService.getDiseasebyString(DN(searchFlag)).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful)
                    setUI(response.body()!!)
            }

            override fun onFailure(call: Call<Disease>, t: Throwable) {
                println(t.message)
            }

        })
    }

    private fun getDiseaseInfo(searchFlag : Long) {
        diseaseService.getDiseasebyID(searchFlag).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful)
                    setUI(response.body()!!)
            }

            override fun onFailure(call: Call<Disease>, t: Throwable) {
                println(t.message)
            }

        })
    }

    private fun setUI(disease: Disease) {
       binding.DinfoName.text = disease.name
       binding.DinfoDepart.text = disease.department
       binding.DinfoCause.text = disease.cause
       binding.DinfoExplain.text = disease.info
       binding.DinfoSymptom.text = disease.symptom
    }
}