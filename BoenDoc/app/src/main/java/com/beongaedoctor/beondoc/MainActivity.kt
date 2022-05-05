package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beongaedoctor.beondoc.databinding.ActivityChatBinding
import com.beongaedoctor.beondoc.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder

class MainActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mainbinding: ActivityMainBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mainbinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chatBtn.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
        }

        val sp = getSharedPreferences("shared", MODE_PRIVATE)
        val gson = GsonBuilder().create()

        if (sp != null) {
            val gsonMemberInfo = sp?.getString("memberInfo","")
            val testMemberInfo : Member = gson!!.fromJson(gsonMemberInfo, Member::class.java)
            binding.username.text = testMemberInfo.name
        }
        else {
            binding.username.text = "사용자X"
        }



    }

    override fun onStart() {
        super.onStart()

        binding.mypageBtn.setOnClickListener {
            val mypageIntent = Intent(this, MainMypageActivity::class.java)
            startActivity(mypageIntent)
        }
    }


}