package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.beongaedoctor.beondoc.App.Companion.context
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignUpBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var SUABinding: ActivitySignUpBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = SUABinding!!
    var member: Member = Member()


    //비밀번호 확인 일치 여부. false 일때는 계속 버튼 비활성화
    var passwordAccord = false

    //이메일 형식 유효성
    var emailValidation = false

    //이메일 중복 검사
    var emailnotAlreadyExist = false



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
                member.gender = position.toLong()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                member.gender = -1 //디버깅용
            }
        }


        binding.idSamebtn.setOnClickListener {
            checkIDNotExist(binding.email.text.toString())
        }


        binding.singupNextBtn.setOnClickListener {
            //제한 조건을 충족하면 User data class에 값 넣기
            if (passwordAccord && emailValidation && emailnotAlreadyExist) {
                saveUser()

                //문제 없을 시 로그인 화면으로 이동 (혹은 자동 로그인 후 메인으로 이동)
                val BEIntent = Intent(this, BasicExamActivity::class.java)
                BEIntent.putExtra("member", member)
                BEIntent.putExtra("isRevise", false)
                startActivity(BEIntent)
            }
            else if (!passwordAccord) {
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
            }
            else if (!emailValidation) {
                Toast.makeText(this, "이메일 형식을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
            else if (!emailnotAlreadyExist) {
                Toast.makeText(this, "이메일 증복검사를 해주세요.", Toast.LENGTH_LONG).show()
            }


        }
    }



    private fun saveUser() {
        member.loginId = binding.email.text.toString()
        member.password = binding.password.text.toString()
        member.name = binding.name.text.toString()
        member.height = binding.height.text.toString()
        member.weight = binding.weight.text.toString()
        //user.sex 는 spinner 선택 시 저장
        member.age = binding.age.text.toString()
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

    private fun checkIDNotExist(email : String) {
        val retrofit = RetrofitClass.getInstance()
        val memberService = retrofit.create(MemberService::class.java)

        memberService!!.checkAlreadyID(Emailcheck(email)).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    if (response.body() == 0) {
                        emailnotAlreadyExist = true
                        binding.idSame.text = "사용 가능한 이메일입니다."
                        binding.idSame.setTextColor(Color.parseColor("#72E5E5"))
                    }
                    else {
                        emailnotAlreadyExist = false
                        binding.idSame.text = "이미 존재하는 이메일입니다."
                        binding.idSame.setTextColor(Color.parseColor("#FF0000"))
                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(context(), "서버 통신 에러", Toast.LENGTH_SHORT).show()
            }
        })

    }

}