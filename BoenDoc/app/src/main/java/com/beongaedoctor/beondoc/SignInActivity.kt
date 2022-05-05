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
    private var sp : SharedPreferences? = null
    private var gson : Gson? = null

    var dialog : LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //서버 연결
        retrofit = RetrofitClass.getInstance()
        val profileService = retrofit.create(MemberService::class.java)

        //데이터 저장
        sp = getSharedPreferences("shared",MODE_PRIVATE);
        gson = GsonBuilder().create()
        dialog = LoadingDialog(this)

        /*
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

         */
        SIABinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onStart() {
        super.onStart()

        if (sp!!.getBoolean("auto", false)) {
            binding.autologin.isChecked = true

            val sharedEmail = sp!!.getString("Email", null)
            val sharedPw = sp!!.getString("Pw", null)

            if (sharedEmail != null && sharedPw != null) {
                requestLogin(sharedEmail, sharedPw, this)
            }
        }


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

        binding.btnSignup.setOnClickListener {
            val siginupIntent = Intent(this, SignUpActivity::class.java) //여기에 회원가입 뷰
            startActivity(siginupIntent)
        }




    }



    fun requestLogin(email : String, pw : String, SIAContext : Context) {
        val loginInfo = Login(email, pw)
        val loginService = retrofit.create(LoginService::class.java)

        loginService.requestLogin2(loginInfo).enqueue(object : Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {

                if (response.isSuccessful) {
                    println("로그인성공")
                    println(response.errorBody()?.toString())
                    println(response.body().toString())


                    //기기에도 정보 저장 - SharedPreferences
                    val memberInfo = gson!!.toJson(response.body(), Member::class.java)
                    val editor : SharedPreferences.Editor = sp!!.edit()
                    editor.putString("memberInfo", memberInfo)
                    editor.commit()

                    //로딩창 종료
                    dialog!!.dismiss()

                    //유효하면 메인 액티비티로 이동
                    Toast.makeText(SIAContext, "로그인 성공", Toast.LENGTH_SHORT).show()


                    if (binding.autologin.isChecked) {
                        editor.putString("Email", email)
                        editor.putString("Pw", pw)
                        editor.putBoolean("auto", true)
                        editor.commit()
                    }

                    gotoMain(SIAContext)
                }
                else {
                    //로딩창 종료
                    dialog!!.dismiss()
                    //println("로그인 연결은 성공, 근데 통신은 안됨")
                    println(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                //println(t.message)
                //println("여기임ㅋㅋ")
                Toast.makeText(SIAContext, "접속 실패 하지만 넘어가", Toast.LENGTH_LONG).show()
                val mainIntent = Intent(SIAContext, MainActivity::class.java)
                startActivity(mainIntent)
            }

        })

        /*
        loginService.requestLogin(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                if (response.isSuccessful) {
                    println("로그인성공")
                    println(response.errorBody()?.toString())
                    println(response.body().toString())
                    val member = response.body()?.let { saveMember(it) }

                    //기기에도 정보 저장 - SharedPreferences
                    val memberInfo = gson!!.toJson(member, Member::class.java)
                    val editor : SharedPreferences.Editor = sp!!.edit()
                    editor.putString("memberInfo", memberInfo)
                    editor.commit()

                    //로딩창 종료
                    dialog!!.dismiss()

                    //유효하면 메인 액티비티로 이동
                    Toast.makeText(SIAContext, "로그인 성공", Toast.LENGTH_SHORT).show()


                    if (binding.autologin.isChecked) {
                        editor.putString("Email", email)
                        editor.putString("Pw", pw)
                        editor.putBoolean("auto", true)
                        editor.commit()
                    }

                    gotoMain(SIAContext)
                }
                else {
                    //로딩창 종료
                    dialog!!.dismiss()
                    //println("로그인 연결은 성공, 근데 통신은 안됨")
                    println(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                //println(t.message)
                //println("여기임ㅋㅋ")
                Toast.makeText(SIAContext, "접속 실패 하지만 넘어가", Toast.LENGTH_LONG).show()
                val mainIntent = Intent(SIAContext, MainActivity::class.java)
                startActivity(mainIntent)
            }

        })
*/

    }

    fun gotoMain(SIAContext : Context) {
        val mainIntent = Intent(SIAContext, MainActivity::class.java)
        startActivity(mainIntent)

    }

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

