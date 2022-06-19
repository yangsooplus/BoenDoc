package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.beongaedoctor.beondoc.databinding.ActivityMyInfoBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MyInfoActivity : AppCompatActivity() {
    // 전역 변수로 바인딩 객체 선언
    private var minfobinding: ActivityMyInfoBinding? = null
    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = minfobinding!!

    private lateinit var retrofit: Retrofit //retrofit
    private var memberService : MemberService? = null //회원 api
    lateinit var memberInfo : Member //회원 정보
    var autoLogin = false //자동로그인 선택 여부
    lateinit var dialog : LoadingDialog //로딩창


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minfobinding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        memberService = retrofit.create(MemberService::class.java)

        //기기에 저장된 유저 정보, 자동로그인 설정 불러오기
        memberInfo = App.prefs.getMember("memberInfo", "")
        autoLogin = App.prefs.getBool("AUTOLOGIN", false)
        dialog = LoadingDialog(this) //로딩창 가져오기
    }

    override fun onStart() {
        super.onStart()

        //성별 선택
        binding.resexSpinner.adapter = ArrayAdapter.createFromResource(
            this, R.array.sexitemList, android.R.layout.simple_spinner_item
        )
        binding.resexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                memberInfo!!.gender = position.toLong() //여성 - 0, 남성 - 1로 설정
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //기존 유저 정보를 입력란의 hint 로 표시해준다.
        binding.reusername.hint = memberInfo!!.name
        binding.resexSpinner.setSelection(memberInfo!!.gender.toInt())
        binding.reuserage.hint = memberInfo!!.age
        binding.reuserheight.hint = memberInfo!!.height
        binding.reuserweight.hint = memberInfo!!.weight
        binding.autologinCheck.isChecked = autoLogin


        //이름
        binding.reusernameBtn.setOnClickListener {
            if(binding.reusername.text.isNotBlank()) //변경사항이 있으면 반영한다.
                memberInfo!!.name = binding.reusername.text.toString()
        }

        //나이
        binding.reuserageBtn.setOnClickListener {
            if(binding.reuserage.text.isNotBlank())
                memberInfo!!.age = binding.reuserage.text.toString()
        }

        //신장
        binding.reuserheightBtn.setOnClickListener {
            if(binding.reuserheight.text.isNotBlank())
                memberInfo!!.height = binding.reuserheight.text.toString()

        }

        //체중
        binding.reuserweightBtn.setOnClickListener {
            if(binding.reuserweight.text.isNotBlank())
                memberInfo!!.weight = binding.reuserweight.text.toString()

        }

        //수정완료 버튼
        binding.reviseDone.setOnClickListener {
            reviseMyInfo(this) //정보 수정
        }
    }

    //뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        reviseMyInfo(this)
    }

    //회원 정보를 수정한다
    private fun reviseMyInfo(context : Context) {

        dialog!!.show() //통신하는 동안 로딩창을 표시

        //기기에도 정보 저장 - SharedPreferences
        App.prefs.setMember("memberInfo", memberInfo)

        if (binding.autologinCheck.isChecked) //오토 로그인 설정에 맞게 저장한다.
            App.prefs.setBool("AUTOLOGIN", true)
        else
            App.prefs.setBool("AUTOLOGIN", false)


        //DB 정보 수정 요청
        memberService!!.reviseProfile(memberInfo!!.id, getUpdateMember(memberInfo!!)
        ).enqueue(object : Callback<UpdateMemberResponse> {
            override fun onResponse(call: Call<UpdateMemberResponse>, response: Response<UpdateMemberResponse>) {
                if (response.isSuccessful) {
                    dialog!!.dismiss() //로딩창 삭제
                    Toast.makeText(context, "회원 정보 수정", Toast.LENGTH_LONG).show() //변경 알림
                    val mypageIntent = Intent(context, MainMypageActivity::class.java)
                    startActivity(mypageIntent) //마이페이지 메인으로 이동
                }
            }

            override fun onFailure(call: Call<UpdateMemberResponse>, t: Throwable) {
                Toast.makeText(context, "오류 발생", Toast.LENGTH_LONG).show() //오류 알림
                dialog!!.dismiss() //로딩창 삭제
            }
        })
    }


    //멤버 정보 수정 통신에는 id가 미포함.
    private fun getUpdateMember(member: Member): UpdateMember {
        return UpdateMember(
            member.loginId,
            member.name,
            member.age,
            member.height,
            member.weight,
            member.gender,
            member.drug,
            member.social,
            member.family,
            member.trauma,
            member.femininity
        )
    }


}