package com.beongaedoctor.beondoc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
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
        //뷰 바인딩
        BEbinding = ActivityBasicExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Intent로 SignUpActivity에서 입력한 유저 정보 받아오기
        member = intent.getSerializableExtra("member") as Member

        //수정 버전이면 isRevis가 true
        isRevise = intent.getBooleanExtra("isRevise", false)

        //앞서 입력한 유저 정보에서 남성이면 여성력(산과력) PASS
        if (member.gender == 1L) isMan = true

        //첫번째 페이지(DrugFragment) 셋팅
        supportFragmentManager.beginTransaction()
            .add(R.id.framelayout, fragmentArray[0], "drug")
            .commit()

        //수정 버전인 경우 기존에 입력했던 값을 불러와서 UI에 뿌려주기 위해 미리 Fragment 를 생성한다.
        if (isRevise) {
            supportFragmentManager.beginTransaction().add(R.id.framelayout, fragmentArray[1], "Social").hide( fragmentArray[1])
                .add(R.id.framelayout, fragmentArray[2], "Family").hide( fragmentArray[2])
                .add(R.id.framelayout, fragmentArray[3], "Physical").hide( fragmentArray[3])
                .add(R.id.framelayout, fragmentArray[4], "Woman").hide(fragmentArray[4]).commit()
        }

    }

    override fun onStart() {
        super.onStart()

        //UI 갱신을 onCreateView 다음인 onStart 에서 시켜준다. onCreate 에서 하면 갱신이 안 된다.
        if (isRevise) { //수정 버전인 경우
            if (member.drug.isNotEmpty()) //저장된 정보가 있을 경우
                fragmentArray[0].view?.findViewById<EditText>(R.id.drugedittext)?.setText(member.drug) //해당하는 editText 에 값 채운다
            if (member.social.isNotEmpty())
                fragmentArray[1].view?.findViewById<EditText>(R.id.socialedittext)?.setText(member.social)
            if (member.family.isNotEmpty())
                fragmentArray[2].view?.findViewById<EditText>(R.id.familyedittext)?.setText(member.family)
            if (member.trauma.isNotEmpty())
                fragmentArray[3].view?.findViewById<EditText>(R.id.physicaledittext)?.setText(member.trauma)
            //산과력은 string을 또 분해해야 하니 과감하게 포기한다.
        }


        binding.nextBtn.setOnClickListener { //다음 버튼
            switchFragment(true)
        }

        binding.prevBtn.setOnClickListener { //이전 버튼
            switchFragment(false)
        }
    }

    //다음, 이전 버튼을 눌렀을 경우 실행.
    //next 가 참이면 다음 버튼을 누른 경우이다.
    private fun switchFragment( next : Boolean) {

        if (next && ((!isMan && pageNum == 4) || (isMan && pageNum == 3))) { //(여성+산과력) (남성+외상력) 입력 후 다음을 누르면 완료
            setConfirmDialogue() //완료 한다는 Dialogue를 띄워준다 (추후 추가)
            return
        }
        else if (!next && pageNum < 1) //0페이지에서 이전을 누르면 무반응
            return

        //----- 이상 특수한 경우 처리 완 -----


        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragmentArray[pageNum]) //현재 페이지 숨김

        if (next) pageNum++ else pageNum-- //페이지 바꾸기



        when (pageNum) {
            0 -> { //drugFragment는 액티비티 생성 시 만들었음
                transaction.show(fragmentArray[0])
            }
            1 -> { //최초 문진 시 Fragment 생성
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



    private fun setBasicExam()  { //문진 내용을 member 클래스 객체에 집어넣기
        member.drug = fragmentArray[0].requireView().findViewById<EditText>(R.id.drugedittext).text.toString()
        member.social = fragmentArray[1].requireView().findViewById<EditText>(R.id.socialedittext).text.toString()
        member.family = fragmentArray[2].requireView().findViewById<EditText>(R.id.familyedittext).text.toString()
        member.trauma = fragmentArray[3].requireView().findViewById<EditText>(R.id.physicaledittext).text.toString()

        if (isMan) return //남성인 경우 산과력은 PASS

        val womanFragment = fragmentArray[4].requireView()

        //빈 string에 해당하는 항목을 이어붙이는 형태
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

        if (member.drug.equals(""))
            member.drug = "없음"

        if (member.social.equals(""))
            member.social = "없음"

        if (member.family.equals(""))
            member.family = "없음"

        if (member.trauma.equals(""))
            member.trauma = "없음"

        if (member.femininity.equals(""))
            member.femininity = "없음"

    }



    private fun saveMember2Server(member_: Member) { //서버에 멤버 정보 저장
        val retrofit = RetrofitClass.getInstance()
        val memberService = retrofit.create(MemberService::class.java)

        //isRevise인 경우에 PUT으로 정보 수정 추가해야함
        if (isRevise) {
            memberService.reviseProfile(member_.id, getUpdateMember(member_)).enqueue(object : Callback<UpdateMemberResponse> {
                override fun onResponse(call: Call<UpdateMemberResponse>, response: Response<UpdateMemberResponse>) {
                    if (response.isSuccessful) {
                        realConfirm() //id가 수정된 member를 SP에 저장
                    }
                }
                override fun onFailure(call: Call<UpdateMemberResponse>, t: Throwable) {
                    println("그냥 안됨")
                }
            })
        }
        else {
            memberService.setProfile(member_).enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) {
                        //생성 시 id는 0. 서버에 POST로 생성 후 부여 받는 id로 교체
                        member.id = response.body()!!.id
                        realConfirm() //id가 수정된 member를 SP에 저장
                    }
                }
                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Toast.makeText(App.context(), "서버 통신 에러", Toast.LENGTH_SHORT).show()
                    realConfirm() //테스트로 넘어가기 나중에 지워야함~
                }
            })
        }


    }

    private fun setConfirmDialogue() { //구현 예정
        //회원 정보를 마친다는 거시기랑 진짜 확인 하면 서버에 저장
        setBasicExam() //기초 문진을 member에 저장
        saveMember2Server(member) //서버에 member 정보 저장
    }

    private fun realConfirm() { //찐찐 확인
        //기기에 정보 저장 - SharedPreferences
        App.prefs.setMember("memberInfo", member)
        App.noMember = false

        if (isRevise) { //수정 모드면 마이페이지 메인으로 이동
            val mainIntent = Intent(this, MainMypageActivity::class.java)
            startActivity(mainIntent)
        }
        else { //최초 문진이면 메인 액티비티로 이동
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

    }

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