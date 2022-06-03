package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivityDresultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class DResultActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var RABinding: ActivityDresultBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = RABinding!!

    private lateinit var retrofit: Retrofit
    private lateinit var diagnosisService: DiagnosisService
    private lateinit var memberInfo : Member

    var fromChat = false
    private var diseaseName1 : String? = null
    private var diseaseName2 : String? = null
    private var diseaseName3 : String? = null

    private var diseaseID1 : Long? = null
    private var diseaseID2 : Long? = null
    private var diseaseID3 : Long? = null

    private var diseaseProb1 : String? = null
    private var diseaseProb2 : String? = null
    private var diseaseProb3 : String? = null

    private var probability : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RABinding = ActivityDresultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diagnosisService = retrofit.create(DiagnosisService::class.java)

        fromChat = intent.getBooleanExtra("fromChat", false)
        println(fromChat)


        //기기에 저장된 유저 정보
        if (!App.noMember)
            memberInfo = App.prefs.getMember("memberInfo", "")

        if (fromChat) {
            diseaseName1 = intent.getStringExtra("diseaseName1")
            diseaseName2 = intent.getStringExtra("diseaseName2")
            diseaseName3 = intent.getStringExtra("diseaseName3")

            diseaseProb1 = intent.getStringExtra("diseaseProb1")
            diseaseProb2 = intent.getStringExtra("diseaseProb2")
            diseaseProb3 = intent.getStringExtra("diseaseProb3")

            probability = "$diseaseProb1 $diseaseProb2 $diseaseProb3"

            //Flask에서 가져온거를 Spring에 넣기
            if (!App.noMember)
                recordD2DB(Diagnosis2DB(diseaseName1, diseaseName2, diseaseName3, probability))
            else
                setUI()
        }
        else {
            //Spring에서 가져오기
            val diagnosisID = intent.getLongExtra("diagnosisId", 0L)
            getDiagnosis(diagnosisID)
        }
    }

    override fun onStart() {
        super.onStart()

        binding.predD1Text.setOnClickListener {
            gotoDInfo(this, diseaseName1, diseaseID1)
        }

        binding.predD2Text.setOnClickListener {
            gotoDInfo(this, diseaseName2, diseaseID2)
        }

        binding.predD3Text.setOnClickListener {
            gotoDInfo(this, diseaseName3, diseaseID3)
        }

        binding.gotoMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun recordD2DB(d2db : Diagnosis2DB) {
        diagnosisService.recordDiagnosis2DB(memberInfo.id, d2db).enqueue(object : Callback<DiseaseList>{
            override fun onResponse(call: Call<DiseaseList>, response: Response<DiseaseList>) {
                if (response.isSuccessful) {
                    println("record 성공")
                    // UI 셋팅
                    setUI()
                }
            }

            override fun onFailure(call: Call<DiseaseList>, t: Throwable) {
                println(t.message)
            }

        })
    }




    private fun getDiagnosis(diagnosisID : Long) {
        diagnosisService.getDiagnosisByOne(diagnosisID).enqueue(object : Callback<List<DiagnosisNotID>> {
            override fun onResponse(
                call: Call<List<DiagnosisNotID>>,
                response: Response<List<DiagnosisNotID>>
            ) {
                if (response.isSuccessful) {
                    val dList = response.body()!!

                    println(dList)
                    diseaseID1 = dList[0].diseaseId
                    diseaseName1 = dList[0].diseaseName
                    diseaseProb1 = dList[0].percent

                    diseaseID2 = dList[1].diseaseId
                    diseaseName2 =dList[1].diseaseName
                    diseaseProb2 = dList[1].percent

                    diseaseID3 = dList[2].diseaseId
                    diseaseName3 =dList[2].diseaseName
                    diseaseProb3 = dList[2].percent

                    // UI 셋팅
                    setUI()
                }
            }

            override fun onFailure(call: Call<List<DiagnosisNotID>>, t: Throwable) {
                println(t.message)
            }

        })
    }

    private fun setUI() {
        binding.predD1Text.text = "$diseaseName1  $diseaseProb1%"
        binding.predD2Text.text = "$diseaseName2  $diseaseProb2%"
        binding.predD3Text.text = "$diseaseName3  $diseaseProb3%"
    }

    private fun gotoDInfo(context : Context, dName : String? = null, dId : Long? = null) {
        val dInfoIntent = Intent(context, DInfoActivity::class.java)

        if (dId != null)
            dInfoIntent.putExtra("diseaseID", dId)
        else if (dName != null)
            dInfoIntent.putExtra("diseaseName", dName)
        else
            println("gotoDInfo 예상치 못한 결과 발생")

        startActivity(dInfoIntent)
    }



}