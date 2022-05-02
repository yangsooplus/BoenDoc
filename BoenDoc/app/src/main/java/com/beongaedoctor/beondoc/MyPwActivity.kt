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

    private var sp : SharedPreferences? = null
    private var gson : Gson? = null

    private var member: Member? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //뷰 바인딩
        MPABinding = ActivityMyPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        memberService = retrofit.create(MemberService::class.java)

        //데이터 저장
        sp = getSharedPreferences("shared",MODE_PRIVATE);
        gson = GsonBuilder().create()

        //기기에 저장된 유저 정보
        val gsonMemberInfo = sp!!.getString("memberInfo","")
        member = gson!!.fromJson(gsonMemberInfo, Member::class.java)
    }

    override fun onStart() {
        super.onStart()
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

        binding.reviseDone.setOnClickListener {
            if (binding.currentPW.text.isEmpty() || binding.newPW.text.isEmpty() || binding.confirmnewPW.text.isEmpty() ) {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_LONG).show()
            }
            else {
                if (checkPW()) { //비밀번호 일치
                    revisePW(this) //비밀번호 변경
                }
                else { //현재 비밀번호가 일치하지 않음
                    Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                    binding.currentPW.setText("")
                }
            }

        }

    }

    fun checkPW() : Boolean{
        //기존 비밀번호의 일치 여부
        println(member!!.password)
        println(binding.currentPW.text.toString())
        return member!!.password.equals(binding.currentPW.text.toString())
    }

    fun revisePW(context: Context) : Unit {
        //비밀번호 변경
        member!!.password = binding.newPW.text.toString()

        //기기에 정보 저장 - SharedPreferences
        val memberInfo = gson!!.toJson(member, Member::class.java)
        val editor : SharedPreferences.Editor = sp!!.edit()
        editor.putString("memberInfo",memberInfo);
        editor.apply();


        //DB 비밀번호 수정 요청
        memberService!!.reviseProfile(member!!.id, member!!).enqueue(object : Callback<Member>{
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "비밀번호를 변경했습니다..", Toast.LENGTH_LONG).show()//변경 알림
                    val mypageIntent = Intent(context, MainMypageActivity::class.java)
                    startActivity(mypageIntent) //마이페이지 메인으로 이동
                }
            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                println(t.message)

                //나중에 아래 지우기
                Toast.makeText(context, "로컬은 변경했고, DB는 안됐어", Toast.LENGTH_LONG).show()//변경 알림
                val mypageIntent = Intent(context, MainMypageActivity::class.java)
                startActivity(mypageIntent) //마이페이지 메인으로 이동
            }

        })




    }
}