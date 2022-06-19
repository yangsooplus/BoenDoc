package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignInActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var SIABinding: ActivitySignInBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = SIABinding!!

    // Http 통신을 위한 retrofit
    private lateinit var retrofit: Retrofit

    //로딩창
    var dialog : LoadingDialog? = null

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retrofit = RetrofitClass.getInstance() //spring 서버와 연결
        dialog = LoadingDialog(this) //로딩창
        SIABinding = ActivitySignInBinding.inflate(layoutInflater) //뷰 바인딩
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        //자동 로그인이 설정되어 있으면
        if (App.prefs.getBool("AUTOLOGIN", false)) {
            binding.autologin.isChecked = true //체크박스에 체크 표시
            val spID = App.prefs.getString("ID", "") //저장된 이메일 불러오기
            val spPW = App.prefs.getString("PW", "") //저장된 비밀번호 불러오기

            if (!spID.equals("") && !spPW.equals("")) { //정상적으로 잘 불러왔으면
                dialog!!.show() //로딩창 띄우기 -> 통신이 끝날때까지 표시된다.
                requestLogin(spID!!, spPW!!, this) //서버에 로그인 요청
                Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show() //자동 로그인 메세지 출력
            }
        }

        //로그인 버튼 누르면
        binding.btnSignin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPw.text.toString()

            //아이디(이메일), 비밀번호 입력되지 않으면 입력하라고 띄우기
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else {
                //로딩창
                dialog!!.show()
                //로그인 시도 - 유효성 검사
                requestLogin(email, password, this)
            }
        }

        //회원가입 누르면
        binding.btnSignup.setOnClickListener {
            val siginupIntent = Intent(this, SignUpActivity::class.java) //회원가입 창으로
            startActivity(siginupIntent)
        }

        //비회원 진료 누르면
        binding.btnNoMember.setOnClickListener {
            App.noMember = true //비회원임을 App 클래스에 저장. 기능이 회원일 때와 다르게 작동하도록 한다.
            gotoMain(this) //메인 액티비티로
        }
    }


    //로그인 요청
    fun requestLogin(email : String, pw : String, SIAContext : Context) {
        val loginInfo = Login(email, pw) //로그인 클래스
        val loginService = retrofit.create(LoginService::class.java) //로그인 인터페이스

        //로그인 요청을 보내고, 성공 시 해당 유저 정보를 가져온다.
        loginService.requestLogin(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) { //통신이 성공적인 경우
                    if (binding.autologin.isChecked) { //자동로그인이 체크되어 있으면 자동 로그인 여부, 아이디, 비밀번호를 저장한다.
                        App.prefs.setBool("AUTOLOGIN", true)
                        App.prefs.setString("ID", email)
                        App.prefs.setString("PW", pw)
                    }

                    //기기에도 정보를 저장한다 - SharedPreferences
                    //받아온 정보를 Member 형식으로 로컬에 저장
                    App.prefs.setMember("memberInfo", saveMember(response.body()!!, pw))

                    //로딩창 종료
                    dialog!!.dismiss()

                    Toast.makeText(SIAContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                    App.noMember = false //비회원 아님
                    gotoMain(SIAContext) //메인 액티비티로
                }
                else { //통신이 성공적이지 못한 경우. 아이디가 존재하지 않거나 비밀번호가 일치하지 않는 등의 경우에 실행된다.
                    //로딩창 종료
                    dialog!!.dismiss()
                    Toast.makeText(SIAContext, "이메일과 비밀번호를 다시 확인해주세요", Toast.LENGTH_LONG).show()
                }
            }

            //통신에 실패한 경우.
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dialog!!.dismiss() //로딩창 종료
                //println(t.message) - 디버깅 출력
                Toast.makeText(SIAContext, "로그인 오류", Toast.LENGTH_LONG).show()
            }
        })
    }

    //메인 액티비티로 이동
    fun gotoMain(SIAContext : Context) {
        val mainIntent = Intent(SIAContext, MainActivity::class.java)
        startActivity(mainIntent)
    }

    //로그인 리스폰스를 멤버 클래스로 변경
    fun saveMember(body: LoginResponse, pw: String): Member {
        return Member(
            body.id,
            body.loginId,
            pw,
            body.name,
            body.age,
            body.height,
            body.weight,
            body.gender,
            body.drug,
            body.social,
            body.family,
            body.trauma,
            body.femininity
        )
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - mBackWait >=2000 ) {
            mBackWait = System.currentTimeMillis() //첫번째 뒤로가기를 누른 순간의 시간 기록
            Toast.makeText(this, "뒤로가기를 한 번 더 눌러 종료", Toast.LENGTH_LONG).show()
        } else { //2초 이내 빠르게 두번 뒤로가기를 누르면
            finishAffinity() //앱 종료
        }
    }
}

