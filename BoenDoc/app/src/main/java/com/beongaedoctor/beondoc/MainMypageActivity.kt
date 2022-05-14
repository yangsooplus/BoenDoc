package com.beongaedoctor.beondoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beongaedoctor.beondoc.databinding.ActivityMainMypageBinding
import com.google.gson.GsonBuilder

class MainMypageActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mainmpbinding: ActivityMainMypageBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mainmpbinding!!

    lateinit var memberInfo : Member


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰바인딩
        mainmpbinding = ActivityMainMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //유저 정보
        App.prefs.getMember("memberInfo", "")
        binding.username.text = memberInfo.name
        binding.useremail.text = memberInfo.loginId

    }

    override fun onStart() {
        super.onStart()

        //정보 수정
        binding.inforevisebtn.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        //비번 수정
        binding.pwrevisebtn.setOnClickListener {
            val intent = Intent(this, MyPwActivity::class.java)
            startActivity(intent)
        }

        //기초문진 수정
        binding.berevisebtn.setOnClickListener {
            val intent = Intent(this, BasicExamActivity::class.java)
            intent.putExtra("member", memberInfo)
            intent.putExtra("isRevise", true)
            startActivity(intent)
        }

        //진단 기록
        binding.recordbtn.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
    }
}