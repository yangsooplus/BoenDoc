package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivityMyPwBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MyPwActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var MPABinding: ActivityMyPwBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = MPABinding!!

    //비밀번호 확인 일치 여부. false 일때는 계속 버튼 비활성화
    var passwordAccord = false

    private lateinit var retrofit: Retrofit
    private var memberService : MemberService? = null


    lateinit var member: Member

    lateinit var dialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //뷰 바인딩
        MPABinding = ActivityMyPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        memberService = retrofit.create(MemberService::class.java)

        dialog = LoadingDialog(this)

        //기기에 저장된 유저 정보
        member = App.prefs.getMember("memberInfo", "")
    }

    override fun onStart() {
        super.onStart()

        //newPW에 글자가 바뀔때마다 호출.
        binding.newPW.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    val same = (binding.newPW.text.toString() == binding.confirmnewPW.text.toString())
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
            }
        })

        //confirmnewPW 글자가 바뀔때마다 호출.
        binding.confirmnewPW.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()) {
                    val same = (binding.newPW.text.toString() == binding.confirmnewPW.text.toString())
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
            }
        })

        //수정 누르면 실행
        binding.reviseDone.setOnClickListener {
            if (binding.currentPW.text.isEmpty() || binding.newPW.text.isEmpty() || binding.confirmnewPW.text.isEmpty() ) {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else {
                checkPW(this)
            }
        }

        binding.backbtn.setOnClickListener {
            super.onBackPressed()
        }
    }

    //기존 비밀번호랑 일치하면 true
    private fun checkPW(context: Context){
        //기존 비밀번호의 일치 여부

        dialog!!.show()

        memberService!!.passwordCheck(member.id, PWcheck(binding.currentPW.text.toString())).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    dialog!!.dismiss()
                    if (response.body() == 0)
                        revisePW(context)
                    else {
                        Toast.makeText(context, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                        binding.currentPW.setText("")
                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                dialog!!.dismiss()
                Toast.makeText(context, "서버 통신 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //비밀번호 수정 요청
    private fun revisePW(context: Context)  {

        dialog!!.show()

        //비밀번호 변경
        member!!.password = binding.newPW.text.toString()


        //DB 비밀번호 수정 요청
        memberService!!.passwordUpdate(member!!.id, PWcheck(member!!.password)).enqueue(object : Callback<Unit>{
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {

                    dialog!!.dismiss()

                    Toast.makeText(context, "비밀번호를 변경했습니다.", Toast.LENGTH_LONG).show()//변경 알림

                    App.prefs.setMember("memberInfo", member) //서버 변경을 성공하면 로컬도 변경.

                    val mypageIntent = Intent(context, MainMypageActivity::class.java)
                    startActivity(mypageIntent) //마이페이지 메인으로 이동
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                println(t.message)

                //나중에 아래 지우기
                dialog!!.dismiss()
                Toast.makeText(context, "서버 통신 오류", Toast.LENGTH_SHORT).show()//변경 알림
                val mypageIntent = Intent(context, MainMypageActivity::class.java)
                startActivity(mypageIntent) //마이페이지 메인으로 이동
            }
        })
    }


}