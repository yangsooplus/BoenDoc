package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var SIABinding: ActivitySignInBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = SIABinding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_in)

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