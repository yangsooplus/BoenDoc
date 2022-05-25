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

    private lateinit var retrofit: Retrofit
    private var memberService : MemberService? = null


    lateinit var memberInfo : Member
    var autoLogin = false

    lateinit var dialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minfobinding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //서버 연결
        retrofit = RetrofitClass.getInstance()
        memberService = retrofit.create(MemberService::class.java)

        //기기에 저장된 유저 정보
        memberInfo = App.prefs.getMember("memberInfo", "")
        autoLogin = App.prefs.getBool("AUTOLOGIN", false)

        dialog = LoadingDialog(this)
    }

    override fun onStart() {
        super.onStart()

        //성별 선택
        binding.resexSpinner.adapter = ArrayAdapter.createFromResource(
            this, R.array.sexitemList, android.R.layout.simple_spinner_item
        )
        binding.resexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //https://stickode.tistory.com/8
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                memberInfo!!.gender = position.toLong()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                memberInfo!!.gender = -1 //디버깅용
            }
        }

        binding.reusername.hint = memberInfo!!.name
        binding.resexSpinner.setSelection(memberInfo!!.gender.toInt())
        binding.reuserage.hint = memberInfo!!.age
        binding.reuserheight.hint = memberInfo!!.height
        binding.reuserweight.hint = memberInfo!!.weight
        binding.autologinCheck.isChecked = autoLogin



        binding.reusernameBtn.setOnClickListener {
            if(binding.reusername.text.isNotBlank())
                memberInfo!!.name = binding.reusername.text.toString()
        }

        binding.reuserageBtn.setOnClickListener {
            if(binding.reuserage.text.isNotBlank())
                memberInfo!!.age = binding.reuserage.text.toString()
        }

        binding.reuserheightBtn.setOnClickListener {
            if(binding.reuserheight.text.isNotBlank())
                memberInfo!!.height = binding.reuserheight.text.toString()

        }
        binding.reuserweightBtn.setOnClickListener {
            if(binding.reuserweight.text.isNotBlank())
                memberInfo!!.weight = binding.reuserweight.text.toString()

        }

        binding.reviseDone.setOnClickListener {
            reviseMyInfo(this)
        }

    }

    private fun reviseMyInfo(context : Context) {

        dialog!!.show()

        //기기에도 정보 저장 - SharedPreferences
        App.prefs.setMember("memberInfo", memberInfo)

        if (binding.autologinCheck.isChecked)
            App.prefs.setBool("AUTOLOGIN", true)
        else
            App.prefs.setBool("AUTOLOGIN", false)


        //DB 정보 수정 요청
        memberService!!.reviseProfile(memberInfo!!.id, getUpdateMember(memberInfo!!)
        ).enqueue(object : Callback<UpdateMemberResponse> {
            override fun onResponse(call: Call<UpdateMemberResponse>, response: Response<UpdateMemberResponse>) {
                if (response.isSuccessful) {
                    dialog!!.dismiss()
                    Toast.makeText(context, "회원 정보 수정", Toast.LENGTH_LONG).show()//변경 알림
                    val mypageIntent = Intent(context, MainMypageActivity::class.java)
                    startActivity(mypageIntent) //마이페이지 메인으로 이동
                }
            }

            override fun onFailure(call: Call<UpdateMemberResponse>, t: Throwable) {
                println(t.message)
                dialog!!.dismiss()
                //나중에 아래 지우기
                Toast.makeText(context, "로컬은 변경했고, DB는 안됐어", Toast.LENGTH_LONG).show()//변경 알림
                val mypageIntent = Intent(context, MainMypageActivity::class.java)
                startActivity(mypageIntent) //마이페이지 메인으로 이동
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

    //뒤로가기 버튼 눌렀을 때
    override fun onBackPressed() {
        reviseMyInfo(this)
    }
}