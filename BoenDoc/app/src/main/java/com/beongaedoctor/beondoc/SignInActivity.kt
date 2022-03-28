package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
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
    private lateinit var loginService: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val id = "testid"
        //val pw = "testpw"

        //서버 연결
        retrofit = RetrofitClass.getInstance()

        /*
        *
        loginService = retrofit.create(LoginService::class.java)
        loginService.requestLogin(id, pw).enqueue(object : Callback<Login>{
            override fun onFailure(call: Call<Login>, t: Throwable) {
                //실패할 경우 실행
                println("실패")
            }

            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                //성공할 경우 실행
                println(response.body())
            }

        })
        *  */

        /*
        val testclassService = retrofit.create(TestClassService::class.java)
        testclassService.getPost()?.enqueue(object : Callback<TestClass>{
            override fun onResponse(call: Call<TestClass>, response: Response<TestClass>) {
                if (response.isSuccessful) {
                    var result: TestClass? = response.body()
                    println(result?.toString())
                }
                else {
                    println("실패")
                }
            }

            override fun onFailure(call: Call<TestClass>, t: Throwable) {
                println(t.message)
            }

        })
        */

        var sampleProfile = Profile("양수진", 4, "23", "164", "99")

        val profileService = retrofit.create(ProfileService::class.java)
        profileService.setProfile(sampleProfile).enqueue(object : Callback<Profile>{
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    println(sampleProfile.toString() + "전송완료")
                }
                else {
                    println("POST 실패")
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                println(t.message)
            }
        })


        profileService.getAllProfile().enqueue(object : Callback<List<Profile>> {
            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                if (response.isSuccessful) {
                    var result: List<Profile>? = response.body()
                    println(result?.toString())
                }
                else {
                    println("GET 실패")
                }

            }

            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {
                println(t.message)
            }

        })



        SIABinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editEmail = binding.editEmail
        val editPw = binding.editPw



        binding.btnSignin.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPw.text.toString()

            //아이디(이메일), 비밀번호 입력되지 않으면 입력하라고 띄우기
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else {
                //로그인 시도 - 유효성 검사


                //유효하면 메인 액티비티로 이동
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }







        }

        binding.btnSignup.setOnClickListener {
            val siginupIntent = Intent(this, SignUpActivity::class.java) //여기에 회원가입 뷰
            startActivity(siginupIntent)
        }
    }
}

