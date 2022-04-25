package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beongaedoctor.beondoc.databinding.ActivityMyInfoBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder


class MyInfoActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var minfobinding: ActivityMyInfoBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = minfobinding!!

    private var sp : SharedPreferences? = null
    private var gson : Gson? = null
    var memberInfo : Member? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        minfobinding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터 저장
        sp = getSharedPreferences("shared",MODE_PRIVATE)
        gson = GsonBuilder().create()

        val gsonMemberInfo = sp!!.getString("memberInfo","")
        memberInfo = gson!!.fromJson(gsonMemberInfo, Member::class.java)

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

    }

    override fun onStart() {
        super.onStart()

        binding.reviseDone.setOnClickListener {
            reviseMyInfo()
        }

        binding.reusernameBtn.setOnClickListener {
            if(!binding.reusername.text.isNullOrBlank())
                memberInfo!!.name = binding.reusername.text.toString()
        }

        binding.reuserageBtn.setOnClickListener {
            if(!binding.reuserage.text.isNullOrBlank())
                memberInfo!!.age = binding.reuserage.text.toString()
        }

        binding.reuserheightBtn.setOnClickListener {
            if(!binding.reuserheight.text.isNullOrBlank())
                memberInfo!!.height = binding.reuserheight.text.toString()

        }
        binding.reuserweight.setOnClickListener {
            if(!binding.reuserweight.text.isNullOrBlank())
                memberInfo!!.weight = binding.reuserweight.text.toString()

        }

    }

    private fun reviseMyInfo() {
        //기기에도 정보 저장 - SharedPreferences
        val memberInfo_ = gson!!.toJson(memberInfo, Member::class.java)
        val editor : SharedPreferences.Editor = sp!!.edit()
        editor.putString("memberInfo",memberInfo_)
        editor.commit()

        val mypageIntent = Intent(this, MainMypageActivity::class.java)
        startActivity(mypageIntent)
    }
}