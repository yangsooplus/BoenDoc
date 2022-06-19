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

    var fromChat = false //이전 액티비티가 채팅 액티비티면 true, 진단 기록 액티비티면 false
    private var diseaseName1 : String? = null //질병1 이름
    private var diseaseName2 : String? = null //질병2 이름
    private var diseaseName3 : String? = null //질병3 이름

    private var diseaseID1 : Long? = null //질병1 아이디
    private var diseaseID2 : Long? = null //질병2 아이디
    private var diseaseID3 : Long? = null //질병3 아이디

    private var diseaseProb1 : String? = null //질병1 확률
    private var diseaseProb2 : String? = null //질병2 확률
    private var diseaseProb3 : String? = null //질병3 확률

    private var probability : String? = null //통합 확률 "확률1 확률2 확률3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RABinding = ActivityDresultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        diagnosisService = retrofit.create(DiagnosisService::class.java)

        //채팅에서 왔는지 여부 불러오기
        fromChat = intent.getBooleanExtra("fromChat", false)

        //유저 id에 진단 기록을 저장하기 위해 기기에 저장된 유저 정보 불러오기
        if (!App.noMember)
            memberInfo = App.prefs.getMember("memberInfo", "")

        //채팅에서 왔으면 ChatActivity에서 intent로 보낸 진단 정보를 가져온다
        if (fromChat) {
            diseaseName1 = intent.getStringExtra("diseaseName1")
            diseaseName2 = intent.getStringExtra("diseaseName2")
            diseaseName3 = intent.getStringExtra("diseaseName3")

            diseaseProb1 = intent.getStringExtra("diseaseProb1")
            diseaseProb2 = intent.getStringExtra("diseaseProb2")
            diseaseProb3 = intent.getStringExtra("diseaseProb3")

            probability = "$diseaseProb1 $diseaseProb2 $diseaseProb3" //3개의 질병 확률을 하나의 String으로 합친다 (DB에서 필요)


            if (!App.noMember) //회원일 경우, Spring DB에 저장
                recordD2DB(Diagnosis2DB(diseaseName1, diseaseName2, diseaseName3, probability))
            else //비회원일 경우에는 UI만 갱신한다.
                setUI()
        }
        else { //진단 기록 조회에서 왔으면 진단 id로 Spring에서 정보를 가져온다.
            val diagnosisID = intent.getLongExtra("diagnosisId", 0L) //진단 id는 진단 기록 Activity에서 전달 받는다.
            getDiagnosis(diagnosisID)
        }
    }

    override fun onStart() {
        super.onStart()

        //첫번째 질병을 누르면
        binding.predD1Text.setOnClickListener {
            gotoDInfo(this, diseaseName1, diseaseID1) //질병명 or 질병id로 질병 정보를 조회한다.
        }

        //두번째 질병을 누르면
        binding.predD2Text.setOnClickListener {
            gotoDInfo(this, diseaseName2, diseaseID2) //질병명 or 질병id로 질병 정보를 조회한다.
        }

        //세번째 질병을 누르면
        binding.predD3Text.setOnClickListener {
            gotoDInfo(this, diseaseName3, diseaseID3) //질병명 or 질병id로 질병 정보를 조회한다.
        }

        //메인으로 돌아가기를 누르면 메인으로 돌아간다.
        binding.gotoMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //DB에 진단 기록을 저장한다.
    private fun recordD2DB(d2db : Diagnosis2DB) {
        diagnosisService.recordDiagnosis2DB(memberInfo.id, d2db).enqueue(object : Callback<DiseaseList>{ //유저 id에 진단 기록을 저장한다.
            override fun onResponse(call: Call<DiseaseList>, response: Response<DiseaseList>) {
                if (response.isSuccessful) { //성공적으로 통신했으면 (저장했으면)
                    setUI() //UI를 갱신한다.
                }
            }
            override fun onFailure(call: Call<DiseaseList>, t: Throwable) {
                println(t.message)
            }
        })
    }


    //진단 id로 진단 기록을 가져온다
    private fun getDiagnosis(diagnosisID : Long) {
        diagnosisService.getDiagnosisByOne(diagnosisID).enqueue(object : Callback<List<DiagnosisNotID>> {
            override fun onResponse(
                call: Call<List<DiagnosisNotID>>,
                response: Response<List<DiagnosisNotID>>
            ) {
                if (response.isSuccessful) { //통신이 성공적이면
                   val dList = response.body()!!

                    //질병 정보를 옮겨 담는다.
                    diseaseID1 = dList[0].diseaseId
                    diseaseName1 = dList[0].diseaseName
                    diseaseProb1 = dList[0].percent

                    diseaseID2 = dList[1].diseaseId
                    diseaseName2 =dList[1].diseaseName
                    diseaseProb2 = dList[1].percent

                    diseaseID3 = dList[2].diseaseId
                    diseaseName3 =dList[2].diseaseName
                    diseaseProb3 = dList[2].percent

                    setUI() // UI 셋팅
                }
            }
            override fun onFailure(call: Call<List<DiagnosisNotID>>, t: Throwable) {
                println(t.message)
            }
        })
    }

    //UI를 갱신한다.
    private fun setUI() {
        binding.predD1Text.text = diseaseName1
        binding.predD2Text.text = diseaseName2
        binding.predD3Text.text = diseaseName3
    }

    //각 질병을 눌렀을 때, 질병 정보 창으로 이동하며 해당 질병을 조회하기 위한 정보를 전달한다 (이름, id)
    private fun gotoDInfo(context : Context, dName : String? = null, dId : Long? = null) {
        val dInfoIntent = Intent(context, DInfoActivity::class.java)

        if (dId != null) //id가 있는 경우 -> ID를 넘긴다
            dInfoIntent.putExtra("diseaseID", dId)
        else if (dName != null) //id가 없는 경우 -> 이름을 넘긴다 (초회 질병 진단 시)
            dInfoIntent.putExtra("diseaseName", dName)
        else
            println("gotoDInfo 예상치 못한 결과 발생")

        startActivity(dInfoIntent)
    }
}