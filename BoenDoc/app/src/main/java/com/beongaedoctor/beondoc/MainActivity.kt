package com.beongaedoctor.beondoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beongaedoctor.beondoc.databinding.ActivityChatBinding
import com.beongaedoctor.beondoc.databinding.ActivityMainBinding

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

//        binding.mypageBtn.setOnClickListener {
//            val mypageIntent = Intent(this, MypageActivity::class.java)
//            startActivity(mypageIntent)
//        }

    }
}