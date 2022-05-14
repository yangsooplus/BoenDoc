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

        //뷰바인딩
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //멤버 정보 불러와서 이름 보여주기
        val memberInfo = App.prefs.getMember("memberInfo", "")
        binding.username.text = memberInfo.name
    }

    override fun onStart() {
        super.onStart()

        //채팅 액티비티로
        binding.chatBtn.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
        }

        //마이페이지 액티비티로
        binding.mypageBtn.setOnClickListener {
            val mypageIntent = Intent(this, MainMypageActivity::class.java)
            startActivity(mypageIntent)
        }
    }


}