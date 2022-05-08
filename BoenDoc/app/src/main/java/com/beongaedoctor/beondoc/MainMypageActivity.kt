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
        mainmpbinding = ActivityMainMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = getSharedPreferences("shared", MODE_PRIVATE)
        val gson = GsonBuilder().create()

        if (sp != null) {
            val gsonMemberInfo = sp!!.getString("memberInfo","")
            memberInfo = gson!!.fromJson(gsonMemberInfo, Member::class.java)
            binding.username.text = memberInfo.name
            binding.useremail.text = memberInfo.loginId
        }
        else {
            binding.username.text = "사용자X"
        }
    }

    override fun onStart() {
        super.onStart()

        binding.inforevisebtn.setOnClickListener {
            val intent = Intent(this, MyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.pwrevisebtn.setOnClickListener {
            val intent = Intent(this, MyPwActivity::class.java)
            startActivity(intent)
        }

        binding.berevisebtn.setOnClickListener {
            val intent = Intent(this, BasicExamActivity::class.java)
            intent.putExtra("member", memberInfo)
            intent.putExtra("isRevise", true)
            startActivity(intent)

        }

        binding.recordbtn.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
    }
}