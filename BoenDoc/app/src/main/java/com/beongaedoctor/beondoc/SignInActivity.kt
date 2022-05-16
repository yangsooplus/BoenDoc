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

    private lateinit var retrofit: Retrofit

    var dialog : LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //서버 연결
        retrofit = RetrofitClass.getInstance()


        dialog = LoadingDialog(this)

        SIABinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()


        if (App.prefs.getBool("AUTOLOGIN", false)) {
            binding.autologin.isChecked = true
            val spID = App.prefs.getString("ID", "")
            val spPW = App.prefs.getString("PW", "")

            if (!spID.equals("") && !spPW.equals(""))
                requestLogin(spID!!, spPW!!, this)
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
            val siginupIntent = Intent(this, SignUpActivity::class.java) //여기에 회원가입 뷰
            startActivity(siginupIntent)
        }




    }


    //로그인 요청
    fun requestLogin(email : String, pw : String, SIAContext : Context) {
        val loginInfo = Login(email, pw) //로그인 클래스
        val loginService = retrofit.create(LoginService::class.java) //로그인 인터페이스

        //로그인 정보 -> 유저 정보
        loginService.requestLogin(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if (response.isSuccessful) {
                    if (binding.autologin.isChecked) {
                        App.prefs.setBool("AUTOLOGIN", true)

                        App.prefs.setString("ID", email)
                        App.prefs.setString("PW", pw)
                    }



                    //기기에도 정보 저장 - SharedPreferences
                    //받아온 정보를 Member 형식으로 로컬에 저장
                    App.prefs.setMember("memberInfo", saveMember(response.body()!!))

                    //로딩창 종료
                    dialog!!.dismiss()

                    //유효하면 메인 액티비티로 이동
                    Toast.makeText(SIAContext, "로그인 성공", Toast.LENGTH_SHORT).show()

                    gotoMain(SIAContext) //메인 액티비티로
                }
                else {
                    //로딩창 종료
                    dialog!!.dismiss()
                    //println("로그인 연결은 성공, 근데 통신은 안됨")
                    println(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(SIAContext, "접속 실패 하지만 넘어가", Toast.LENGTH_LONG).show()
                val mainIntent = Intent(SIAContext, MainActivity::class.java)
                startActivity(mainIntent)
            }
        })
    }

    //메인 액티비티로
    fun gotoMain(SIAContext : Context) {
        val mainIntent = Intent(SIAContext, MainActivity::class.java)
        startActivity(mainIntent)
    }

    //로그인 리스폰스를 멤버 클래스로 변경
    fun saveMember(body: LoginResponse): Member {
        return Member(
            body.id,
            body.loginId,
            body.password,
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
}

