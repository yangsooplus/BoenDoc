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
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignUpBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
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

    private lateinit var retrofit: Retrofit
    private var memberService : MemberService? = null

    private var sp : SharedPreferences? = null
    private var gson : Gson? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰 바인딩
        SUABinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        //retrofit = RetrofitClass.getInstance()
        //memberService = retrofit.create(MemberService::class.java)

        //데이터 저장
        sp = getSharedPreferences("shared",MODE_PRIVATE);
        gson = GsonBuilder().create()



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





        binding.singupNextBtn.setOnClickListener {
            //제한 조건을 충족하면 User data class에 값 넣기
            if (passwordAccord && emailValidation) {
                saveUser()

                //문제 없을 시 로그인 화면으로 이동 (혹은 자동 로그인 후 메인으로 이동)
                val BEIntent = Intent(this, BasicExamActivity::class.java)
                BEIntent.putExtra("member", member)
                BEIntent.putExtra("isRevise", false)
                startActivity(BEIntent)

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
        //member.anamnesis = cutAnamesis(binding.anamnesis.text.toString())

        return
        //user 정보 db에 저장.

        /*
        memberService!!.setProfile(member).enqueue(object : Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                if (response.isSuccessful)
                    println("쳐넣음")
                else
                    println("연결은했는데안됨")
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                println("그냥 안됨")
            }
        })
        */

        //기기에도 정보 저장 - SharedPreferences
        //val memberInfo = gson!!.toJson(member, Member::class.java)
        //val editor : SharedPreferences.Editor = sp!!.edit()
        //editor.putString("memberInfo",memberInfo);
        //editor.apply();

        //테스트용 불러오기 - SharedPreferences
        //val gsonMemberInfo = sp!!.getString("memberInfo","")
        //val testMemberInfo : Member = gson!!.fromJson(gsonMemberInfo, Member::class.java)
        //println(testMemberInfo.name + "정보 불러왔음")

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