package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.beongaedoctor.beondoc.Fragment.*
import com.beongaedoctor.beondoc.databinding.ActivityBasicExamBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasicExamActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var BEbinding: ActivityBasicExamBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = BEbinding!!

    private var pageNum = 0

    lateinit var member : Member
    private val fragmentArray = arrayOf(DrugFragment(), SocialFragment(), FamilyFragment(), PhysicalFragment(), WomanFragment())
    var isMan = false
    var isRevise = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BEbinding = ActivityBasicExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        member = intent.getSerializableExtra("member") as Member
        isRevise = intent.getBooleanExtra("isRevise", false)
        println(member)

        if (member.gender == 1L) isMan = true

        supportFragmentManager.beginTransaction()
            .add(R.id.framelayout, fragmentArray[0], "drug")
            .commit()

        if (isRevise) {
            supportFragmentManager.beginTransaction().add(R.id.framelayout, fragmentArray[1], "Social").hide( fragmentArray[1])
                .add(R.id.framelayout, fragmentArray[2], "Family").hide( fragmentArray[2])
                .add(R.id.framelayout, fragmentArray[3], "Physical").hide( fragmentArray[3])
                .add(R.id.framelayout, fragmentArray[4], "Woman").hide(fragmentArray[4]).commit()
        }

    }

    override fun onStart() {
        super.onStart()
        if (isRevise) {
            if (member.drug.isNotEmpty())
                fragmentArray[0].view?.findViewById<EditText>(R.id.drugedittext)?.setText(member.drug)
            if (member.drug.isNotEmpty())
                fragmentArray[1].view?.findViewById<EditText>(R.id.socialedittext)?.setText(member.social)
            if (member.drug.isNotEmpty())
                fragmentArray[2].view?.findViewById<EditText>(R.id.familyedittext)?.setText(member.family)
            if (member.drug.isNotEmpty())
                fragmentArray[3].view?.findViewById<EditText>(R.id.physicaledittext)?.setText(member.trauma)
        }


        binding.nextBtn.setOnClickListener {
            switchFragment(true)
        }

        binding.prevBtn.setOnClickListener {
            switchFragment(false)
        }
    }


    private fun switchFragment( next : Boolean) {

        if (next && ((!isMan && pageNum == 4) || (isMan && pageNum == 3))) {
            setConfirmDialogue()
            return
        }
        else if (!next && pageNum < 1)
            return


        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragmentArray[pageNum]) //현재 페이지 숨김

        if (next) pageNum++ else pageNum-- //페이지 바꾸기



        when (pageNum) {
            0 -> {
                transaction.show(fragmentArray[0])
            }
            1 -> {
                if (supportFragmentManager.findFragmentByTag("Social") == null){
                    transaction.add(R.id.framelayout, fragmentArray[1], "Social").commit()
                }
                else {
                    transaction.show(fragmentArray[1]).commit()
                }
            }
            2 -> {
                if (supportFragmentManager.findFragmentByTag("Family") == null){
                    transaction.add(R.id.framelayout, fragmentArray[2], "Family").commit()
                }
                else {
                    transaction.show(fragmentArray[2]).commit()
                }
            }
            3 -> {
                if (supportFragmentManager.findFragmentByTag("Physical") == null){
                    transaction.add(R.id.framelayout, fragmentArray[3], "Physical").commit()
                }
                else {
                    transaction.show(fragmentArray[3]).commit()
                }
            }
            4 -> {
                if (supportFragmentManager.findFragmentByTag("Woman") == null){
                    transaction.add(R.id.framelayout, fragmentArray[4], "Woman").commit()
                }
                else {
                    transaction.show(fragmentArray[4]).commit()
                }
            }
        }


    }



    private fun setBasicExam()  {
        member.drug = fragmentArray[0].requireView().findViewById<EditText>(R.id.drugedittext).text.toString()
        member.social = fragmentArray[1].requireView().findViewById<EditText>(R.id.socialedittext).text.toString()
        member.family = fragmentArray[2].requireView().findViewById<EditText>(R.id.familyedittext).text.toString()
        member.trauma = fragmentArray[3].requireView().findViewById<EditText>(R.id.physicaledittext).text.toString()

        if (isMan) return

        val womanFragment = fragmentArray[4].requireView()
        var femininityText = ""
        var et = womanFragment.findViewById<EditText>(R.id.TeditText)
        if (et.text.isNotBlank()) femininityText += "만삭 ${et.text}명,"

        et = womanFragment.findViewById(R.id.PeditText)
        if (et.text.isNotBlank()) femininityText += " 조산 ${et.text}명,"

        et = womanFragment.findViewById(R.id.AeditText)
        if (et.text.isNotBlank()) femininityText += " 유산 ${et.text}명,"

        et = womanFragment.findViewById(R.id.LeditText)
        if (et.text.isNotBlank()) femininityText += " 생존 ${et.text}명,"
       member.femininity = femininityText
    }



    private fun saveMember2Server(member_: Member) {
        val retrofit = RetrofitClass.getInstance()
        val memberService = retrofit.create(MemberService::class.java)

        //isRevise인 경우에 PUT으로 정보 수정 추가해야함
        memberService.setProfile(member_).enqueue(object : Callback<Member> {
            override fun onResponse(call: Call<Member>, response: Response<Member>) {
                if (response.isSuccessful) {
                    member.id = response.body()!!.id
                }

            }

            override fun onFailure(call: Call<Member>, t: Throwable) {
                println("그냥 안됨")
            }
        })
    }

    private fun setConfirmDialogue() {
        realConfirm()
    }

    private fun realConfirm() {
        setBasicExam()
        saveMember2Server(member)

        //기기에도 정보 저장 - SharedPreferences
        val sp = getSharedPreferences("shared",MODE_PRIVATE);
        val gson = GsonBuilder().create()
        val memberInfo = gson!!.toJson(member, Member::class.java)
        val editor : SharedPreferences.Editor = sp!!.edit()
        editor.putString("memberInfo", memberInfo);
        editor.apply();

        if (isRevise) {
            val mainIntent = Intent(this, MainMypageActivity::class.java)
            startActivity(mainIntent)
        }
        else {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

    }

}