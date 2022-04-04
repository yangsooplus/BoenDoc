package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val id = "testid"
        //val pw = "testpw"

        //서버 연결
        retrofit = RetrofitClass.getInstance()



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



        val profileService = retrofit.create(MemberService::class.java)



        profileService.getAllProfile().enqueue(object : Callback<MemberList> {
            override fun onResponse(call: Call<MemberList>, response: Response<MemberList>) {
                if (response.isSuccessful) {
                    var result: MemberList? = response.body()
                    //Log.d("get", result.toString())
                    println("뭐야 GET 여기야")
                    println(result?.toString())
                }
                else {
                    println("GET 실패")
                    println(response.errorBody()!!.string())
                }

            }

            override fun onFailure(call: Call<MemberList>, t: Throwable) {
                println("GET 오류")
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
                requestLogin(email, password)

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



    fun requestLogin(email : String, pw : String) {
        val loginInfo = Login(email, pw)
        val loginService = retrofit.create(LoginService::class.java)

        loginService.requestLogin(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    println("로그인성공")
                }
                else {
                    println("로그인 연결은 성공, 근데 통신은 안됨")
                    println(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                println(t.message)
            }

        })


    }
}

