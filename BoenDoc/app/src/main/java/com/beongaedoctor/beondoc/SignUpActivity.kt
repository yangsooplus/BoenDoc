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
                //text가 바뀔 때마다 이메일 유효성 검사
                emailValidation = checkEmail()
            }
            override fun afterTextChanged(p0: Editable?) {
                //text 변경 후 호출
                //p0은 변경 후 문자열이 담겨있음
            }
        })

        //비밀번호 확인
        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) { //무언가 입력되어 있으면
                    val same = (binding.password.text.toString() == binding.confirmpassword.text.toString()) //비밀번호와 비밀번호확인란의 내용이 같은지 확인
                    passwordAccord = if (same) { //같을 경우의 텍스트 내용과 숫자로 바꿈
                        binding.passwordSame.setText("비밀번호가 일치합니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#72E5E5"))
                        true
                    } else { //다른 경우의 텍스트 내용과 숫자로 바꿈
                        binding.passwordSame.setText("비밀번호가 일치하지 않습니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#FF0000"))
                        false
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.confirmpassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) { //무언가 입력되어 있으면
                    val same = (binding.password.text.toString() == binding.confirmpassword.text.toString()) //비밀번호와 비밀번호확인란의 내용이 같은지 확인
                    passwordAccord = if (same) { //같을 경우의 텍스트 내용과 숫자로 바꿈
                        binding.passwordSame.setText("비밀번호가 일치합니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#72E5E5"))
                        true
                    } else { //다른 경우의 텍스트 내용과 숫자로 바꿈
                        binding.passwordSame.setText("비밀번호가 일치하지 않습니다.")
                        binding.passwordSame.setTextColor(Color.parseColor("#FF0000"))
                        false
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })


        //성별 선택
        binding.sexSpinner.adapter = ArrayAdapter.createFromResource( //s스피너에 어댑터 연결
        this, R.array.sexitemList, android.R.layout.simple_spinner_item //sexitemList = ["여성", "남성"]
        )

        binding.sexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                member.gender = position.toLong() //NLP 팀원들이 전처리한 대로 0을 여성, 1을 남성으로 설정
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { //아무것도 설정하지 않으면
                member.gender = -1 //디버깅용
            }
        }

        //이메일 중복 확인
        binding.idSamebtn.setOnClickListener {
            checkIDNotExist(binding.email.text.toString())
        }

        //다음 버튼
        binding.singupNextBtn.setOnClickListener {
            //제한 조건을 충족하면 Member data class에 값 넣기
            if (passwordAccord && emailValidation && emailnotAlreadyExist) {
                saveMember()

                //문제 없을 시 기초문진 입력 화면으로 이동
                val BEIntent = Intent(this, BasicExamActivity::class.java)
                BEIntent.putExtra("member", member) //입력한 회원 정보를 member에 담아 전달. member 클래스는 Intent로 전달하기 위해 직렬화 되어있음.
                BEIntent.putExtra("isRevise", false) //초기 기초문진임을 알림
                startActivity(BEIntent)
            }
            else if (!passwordAccord) { //비밀번호와 비밀번호 확인이 일치하지 않을 경우
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
            }
            else if (!emailValidation) { //입력한 이메일이 이메일 형식이 아닐 경우
                Toast.makeText(this, "이메일 형식을 확인해주세요.", Toast.LENGTH_LONG).show()
            }
            else if (!emailnotAlreadyExist) { //이메일 중복검사를 하지 않은 경우
                Toast.makeText(this, "이메일 증복검사를 해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //member 변수에 사용자가 입력한 정보를 담는 함수
    private fun saveMember() {
        member.loginId = binding.email.text.toString()
        member.password = binding.password.text.toString()
        member.name = binding.name.text.toString()
        member.height = binding.height.text.toString()
        member.weight = binding.weight.text.toString()
        //member.sex 는 spinner 선택 시 자동으로 저장
        member.age = binding.age.text.toString()
    }

    //이메일 형식 유효성을 검증하는 함수.
    fun checkEmail() : Boolean{
        val email_input = binding.email.text.toString().trim() //입력한 이메일의 공백을 없앤다
        if (email_input.isEmpty()) //비워져 있으면 항상 false
            return false

        // 이메일 형식 검사 정규식
        val emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        // 이메일 형식 정규식에 입력한 문자가 일치하면 True
        val isMatching = Pattern.matches(emailValidation, email_input)

        return if (isMatching) { //유효할 경우 이메일 글씨를 검은색으로 변경
            binding.email.setTextColor(Color.parseColor("#000000"))
            true
        } else { //유효하지 않을 경우 이메일 글씨를 빨간색으로 변경
            binding.email.setTextColor(Color.parseColor("#FF0000"))
            false
        }
    }

    //이메일 중복 검사
    private fun checkIDNotExist(email : String) {
        val retrofit = RetrofitClass.getInstance() //retrofit 객체 생성/가져오기
        val memberService = retrofit.create(MemberService::class.java) //기존에 interface로 만들어둔 서비스를 생성한다.

        memberService!!.checkAlreadyID(Emailcheck(email)).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) { //통신이 성공적인 경우
                    if (response.body() == 0) { //중복되는 이메일이 없으면
                        emailnotAlreadyExist = true
                        binding.idSame.text = "사용 가능한 이메일입니다."
                        binding.idSame.setTextColor(Color.parseColor("#72E5E5"))
                    }
                    else { //중복되는 이메일이 있으면
                        emailnotAlreadyExist = false
                        binding.idSame.text = "이미 존재하는 이메일입니다."
                        binding.idSame.setTextColor(Color.parseColor("#FF0000"))
                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) { //통신 에러의 경우
                Toast.makeText(context(), "서버 통신 에러", Toast.LENGTH_SHORT).show()
            }
        })
    }
}