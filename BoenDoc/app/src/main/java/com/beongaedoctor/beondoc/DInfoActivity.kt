package com.beongaedoctor.beondoc

import android.content.Intent
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

    //Http통신
    private lateinit var retrofit: Retrofit
    private lateinit var diseaseService : DiseaseService

    //MapActivity에서 검색할 Keyword
    var mapKeyword : String = ""

    lateinit var dialog : LoadingDialog //로딩창

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //뷰바인딩
        DIBinding = ActivityDinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diseaseService = retrofit.create(DiseaseService::class.java)

        val dn = intent.getStringExtra("diseaseName") //DResult에서 질병 이름을 가져온다
        val did = intent.getLongExtra("diseaseID", -1L) //DResult에서 질병 id를 가져온다. 없는 경우에는 -1L을 초기값으로

        dialog = LoadingDialog(this) //로딩 창
        dialog.show() //로딩창 출현

        if (did == -1L) //질병id가 없으면 질병 이름으로 정보 조회
            getDiseaseInfo(dn!!)
        else //질병 id가 있으면 질병 id로 정보 조회
            getDiseaseInfo(did)
    }

    override fun onStart() {
        super.onStart()
        //병원 검색 버튼을 누르면
        binding.gotoHospital.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("mapKeyword", mapKeyword) //진료과를 검색 키워드로
            startActivity(intent)
        }
        //약국 검색 버튼을 누르면
        binding.gotoPharmacy.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("mapKeyword", "Pharm") //약국을 검색 키워드로
            startActivity(intent)
        }
    }

    //질병 이름으로 질병 정보 조회
    private fun getDiseaseInfo(searchFlag : String) {
        diseaseService.getDiseasebyString(DN(searchFlag)).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful) { //통신이 성공적이면
                    mapKeyword = response.body()!!.department //진료과를 검색 키워드로 저장
                    setUI(response.body()!!) //UI 갱신
                }
                dialog.dismiss() //로딩창 종료
            }
            override fun onFailure(call: Call<Disease>, t: Throwable) {
                println(t.message)
                dialog.dismiss() //로딩창 종료
            }
        })
    }

    //질병 id로 질병 정보 조회
    private fun getDiseaseInfo(searchFlag : Long) {
        diseaseService.getDiseasebyID(searchFlag).enqueue(object : Callback<Disease> {
            override fun onResponse(call: Call<Disease>, response: Response<Disease>) {
                if (response.isSuccessful) { //통신이 성공적이면
                    mapKeyword = response.body()!!.department //진료과를 검색 키워드로 저장
                    setUI(response.body()!!) //Ui 갱신
                }
                dialog.dismiss() //로딩창 종료
            }
            override fun onFailure(call: Call<Disease>, t: Throwable) {
                println(t.message)
                dialog.dismiss() //로딩창 종료
            }
        })
    }

    private fun setUI(disease: Disease) { //UI 갱신
       binding.DinfoName.text = disease.name
       binding.DinfoDepart.text = disease.department
       binding.DinfoCause.text = disease.cause
       binding.DinfoExplain.text = disease.info
       binding.DinfoSymptom.text = disease.symptom
    }
}