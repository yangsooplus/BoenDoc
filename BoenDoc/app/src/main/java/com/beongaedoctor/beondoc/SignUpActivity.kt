package com.beongaedoctor.beondoc

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var SUABinding: ActivitySignUpBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = SUABinding!!
    var user: User = User()

    //비밀번호 확인 일치 여부. false 일때는 계속 버튼 비활성화
    var passwordAccord = false

    //이메일 형식 유효성
    var emailValidation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰 바인딩
        SUABinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //이메일 유효성 검사
        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //text 변경 전 호출
                //p0는 변경 전 문자열이 담겨있음
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //text가 바뀔 때마다 호출
                emailValidation = checkEmail()
            }
            override fun afterTextChanged(p0: Editable?) {
                //text 변경 후 호출
                //p0은 변경 후 문자열이 담겨있음
            }

        })


        //비밀번호 확인
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //text 변경 전 호출
                //p0는 변경 전 문자열이 담겨있음
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    val same = (binding.password.text.toString() == binding.confirmpassword.text.toString())
                    if (same) {
                        binding.passwordSame.setText("비밀번호가 일치합니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#72E5E5"))
                        passwordAccord = true
                    }
                    else {
                        binding.passwordSame.setText("비밀번호가 일치하지 않습니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#FF0000"))
                        passwordAccord = false
                    }
                }

            }
            override fun afterTextChanged(p0: Editable?) {
                //text 변경 후 호출
                //p0은 변경 후 문자열이 담겨있음
            }
        })

        binding.confirmpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //text 변경 전 호출
                //p0는 변경 전 문자열이 담겨있음
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    val same = (binding.password.text.toString() == binding.confirmpassword.text.toString())
                    if (same) {
                        binding.passwordSame.setText("비밀번호가 일치합니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#72E5E5"))
                        passwordAccord = true
                    }
                    else {
                        binding.passwordSame.setText("비밀번호가 일치하지 않습니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#FF0000"))
                        passwordAccord = false
                    }
                }

            }
            override fun afterTextChanged(p0: Editable?) {
                //text 변경 후 호출
                //p0은 변경 후 문자열이 담겨있음
            }
        })


        //성별 선택
        binding.sexSpinner.adapter = ArrayAdapter.createFromResource(
        this, R.array.sexitemList, android.R.layout.simple_spinner_item
        )
        binding.sexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //https://stickode.tistory.com/8
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                user.sex = position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                user.sex = -1 //디버깅용
            }
        }









        binding.signupBtn.setOnClickListener {
            //제한 조건을 충족하면 User data class에 값 넣기
            if (passwordAccord && emailValidation)
                saveUser()


            //user 정보 db에 저장.
            //기기에도 저장해두면?


            //문제 없을 시 로그인 화면으로 이동 (혹은 자동 로그인 후 메인으로 이동)
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }
    }


    fun cutAnamesis(data: String): List<String> {
        var anamesis = emptyList<String>()
        if (data.isNotEmpty()) {
            anamesis = data.replace(" ", "").split(',')
        }
        return anamesis
    }

    fun saveUser() {
        user.email = binding.email.text.toString()
        user.password = binding.password.text.toString()
        user.name = binding.name.text.toString()
        user.height = binding.height.text.toString().toDouble()
        user.weight = binding.weight.text.toString().toDouble()
        //user.sex 는 spinner 선택 시 저장
        user.age = binding.age.text.toString().toInt()
        user.anamnesis = cutAnamesis(binding.anamnesis.text.toString())


        println(user) //test
    }

    fun checkEmail():Boolean{
        var email_input = binding.email.text.toString().trim()
        if (email_input.isEmpty())
            return false

        // 이메일 형식 검사 정규식
        val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"



        val isMatching = Pattern.matches(emailValidation, email_input)

        if (isMatching) {
            binding.email.setTextColor(Color.parseColor("#000000"))
            return true
        }
        else {
            binding.email.setTextColor(Color.parseColor("#FF0000"))
            return false
        }
    }
}