package com.beongaedoctor.beondoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.beongaedoctor.beondoc.App.Companion.context
import com.beongaedoctor.beondoc.databinding.ActivityMainMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainMypageActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mainmpbinding: ActivityMainMypageBinding? = null
    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mainmpbinding!!
    //유저 정보
    lateinit var memberInfo : Member


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰바인딩
        mainmpbinding = ActivityMainMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //유저 정보 불러오기 - 상단에 이름과 이메일 표시
        memberInfo = App.prefs.getMember("memberInfo", "")!!
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

        //로그아웃
        binding.signoutbtn.setOnClickListener {
            goToSignIn()
        }

        //탈퇴하기
        binding.leavebtn.setOnClickListener {
            val customDialog = CustomDialog(this)
            customDialog.showCustomDialog("정말로 탈퇴하시겠습니까?") //한번 더 안내창을 출력
            customDialog.setOnClickListener(object : CustomDialog.ButtonClickListener{
                override fun onClicked(yes: Boolean) {
                    if (yes)
                        leaveBeonDoc()
                }
            })
        }
    }

    //뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
        startActivity(intent) //인텐트 이동
        finish() //현재 액티비티 종료
    }

    //회원 탈퇴
    private fun leaveBeonDoc() {
        val retrofit = RetrofitClass.getInstance()
        val memberService = retrofit.create(MemberService::class.java)

        //DB의 유저 id에 맞는 회원 정보를 삭제한다.
        memberService!!.deleteProfile(memberInfo.id).enqueue(object : Callback<Unit>
        {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                goToSignIn()
                Toast.makeText(context(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()//변경 알림
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(context(), "오류 발생", Toast.LENGTH_SHORT).show()//변경 알림
            }

        })
    }

    //SignInActivity로 이동
    private fun goToSignIn() {
        App.prefs.deleteByKey("memberInfo") //저장된 유저 정보를 삭제
        App.prefs.deleteByKey("AUTOLOGIN") //자동로그인 여부를 삭제
        val intent = Intent(this, SignInActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        startActivity(intent) //인텐트 이동
    }
}


