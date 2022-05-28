package com.beongaedoctor.beondoc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beongaedoctor.beondoc.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mainbinding: ActivityMainBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mainbinding!!

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰바인딩
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //멤버 정보 불러와서 이름 보여주기
        val memberInfo = App.prefs.getMember("memberInfo", "")
        binding.username.text = memberInfo?.name
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

        binding.egyBtn.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("mapKeyword", "응급실")
            startActivity(intent)
        }
    }


    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기를 한 번 더 눌러 종료", Toast.LENGTH_LONG).show()
        } else {
            finishAffinity() //앱 종료
        }
    }

}