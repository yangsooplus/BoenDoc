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

    private var pageNum = 0 //현재 사용자가 보고 있는 페이지.

    lateinit var member : Member //사용자 정보를 담을 Member 클래스
    //Fragment를 쉽게 사용하기 위해 미리 초기화하여 배열에 담아두어 사용한다.
    private val fragmentArray = arrayOf(DrugFragment(), SocialFragment(), FamilyFragment(), PhysicalFragment(), WomanFragment())

    var isMan = false //남성이면 true, 여성이면 false
    var isRevise = false //수정중이면 true, 초회 작성이면 false


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

        //첫번째 페이지(DrugFragment)를 화면에 띄워준다.
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
        //UI 갱신을 Fragment의 onCreateView 다음인 onStart 에서 시켜준다. onCreate 에서 하면 프래그먼트의 UI 갱신이 안 된다.
        if (isRevise) { //수정 버전인 경우
            if (member.drug.isNotEmpty()) //저장된 정보가 있을 경우
                fragmentArray[0].view?.findViewById<EditText>(R.id.drugedittext)?.setText(member.drug) //해당하는 editText 에 값을 채워서 보여준다.
            if (member.social.isNotEmpty())
                fragmentArray[1].view?.findViewById<EditText>(R.id.socialedittext)?.setText(member.social)
            if (member.family.isNotEmpty())
                fragmentArray[2].view?.findViewById<EditText>(R.id.familyedittext)?.setText(member.family)
            if (member.trauma.isNotEmpty())
                fragmentArray[3].view?.findViewById<EditText>(R.id.physicaledittext)?.setText(member.trauma)
        }

        binding.nextBtn.setOnClickListener { //다음 버튼 눌렀을 시 실행
            switchFragment(true)
        }
        binding.prevBtn.setOnClickListener { //이전 버튼 눌렀을 시 실행
            switchFragment(false)
        }
    }

    //다음, 이전 버튼을 눌렀을 경우 실행.
    //next가 true면 다음 버튼을 누른 경우이다.
    private fun switchFragment( next : Boolean) {

        if (next && ((!isMan && pageNum == 4) || (isMan && pageNum == 3))) { //(여성+산과력) (남성+외상력) 입력 후 다음을 누르면 완료
            setBasicExam() //기초 문진을 member에 저장
            saveMember2Server(member) //서버에 member 정보 저장
            return
        }
        else if (!next && pageNum < 1) //0페이지에서 이전을 누르면 무반응
            return

        //현재 표시된 페이지 숨김
        //사용자가 입력했던 정보를 유지하고 싶어서 프래그먼트를 생성 후 파괴시키지 않고 표시/숨김으로 처리했다.
        val transaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragmentArray[pageNum])

        if (next) pageNum++ else pageNum-- //페이지 바꾸기

        when (pageNum) { //앞으로 표시할 페이지에 따라 실행
            0 -> { //drugFragment는 액티비티 생성 시 만들었음. 숨겼던 프래그먼트를 보여준다.
                transaction.show(fragmentArray[0])
            }
            1 -> {
                if (supportFragmentManager.findFragmentByTag("Social") == null){ //해당 페이지를 처음 보여줄 경우
                    transaction.add(R.id.framelayout, fragmentArray[1], "Social").commit() //사회력을 태그를 붙여서 FragmentManager에 넣는다.
                }
                else { //이미 보여준 적이 있으면 숨겼던 Fragment를 보여준다.
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



    private fun setBasicExam()  { //문진 내용을 member 클래스 객체에 넣어서 저장하기
        //약물 투약력, 사회력, 가족력, 과거력 및 외상력은 단순히 입력한 text를 저장하기만 하면 된다.
        member.drug = fragmentArray[0].requireView().findViewById<EditText>(R.id.drugedittext).text.toString()
        member.social = fragmentArray[1].requireView().findViewById<EditText>(R.id.socialedittext).text.toString()
        member.family = fragmentArray[2].requireView().findViewById<EditText>(R.id.familyedittext).text.toString()
        member.trauma = fragmentArray[3].requireView().findViewById<EditText>(R.id.physicaledittext).text.toString()

        if (isMan) return //남성인 경우 산과력을 저장하지 않고 끝낸다.

        //여성인 경우 산과력/여성력 Fragment에 대한 처리도 한다.
        val womanFragment = fragmentArray[4].requireView()

        //빈 string에 해당하는 항목을 이어붙이는 형태
        //입력한 숫자를 모델의 데이터 형식에 맞게 조정한다.
        //ex) 만삭 1명, 유산 1명,
        var femininityText = ""
        var et = womanFragment.findViewById<EditText>(R.id.TeditText)

        //사용자가 입력한 항목에 대해서만 문자열을 더해준다.
        if (et.text.isNotBlank()) femininityText += "만삭 ${et.text}명,"

        et = womanFragment.findViewById(R.id.PeditText)
        if (et.text.isNotBlank()) femininityText += " 조산 ${et.text}명,"

        et = womanFragment.findViewById(R.id.AeditText)
        if (et.text.isNotBlank()) femininityText += " 유산 ${et.text}명,"

        et = womanFragment.findViewById(R.id.LeditText)
        if (et.text.isNotBlank()) femininityText += " 생존 ${et.text}명,"

        member.femininity = femininityText //완성된 String을 저장
    }



    private fun saveMember2Server(member_: Member) { //서버에 멤버 정보 저장
        val retrofit = RetrofitClass.getInstance() //http통신을 위해 retrofit 인스턴스를 가져온다(싱글톤)
        val memberService = retrofit.create(MemberService::class.java)


        if (isRevise) { //기초문진을 수정하는 경우
            //유저 id 경로에 갱신된 Member 클래스를 전송한다.
            memberService.reviseProfile(member_.id, getUpdateMember(member_)).enqueue(object : Callback<UpdateMemberResponse> {
                override fun onResponse(call: Call<UpdateMemberResponse>, response: Response<UpdateMemberResponse>) {
                    if (response.isSuccessful) { //통신이 성공적이라면
                        realConfirm() //수정된 정보를 로컬에도 적용
                    }
                }
                override fun onFailure(call: Call<UpdateMemberResponse>, t: Throwable) { //통신에 실패
                    Toast.makeText(App.context(), "서버 통신 에러", Toast.LENGTH_SHORT).show() //메세지를 띄워준다
                }
            })
        }
        else { //처음 기초문진을 작성하는 경우 (회원가입하는 경우)
            //Member 클래스를 전송한다.
            memberService.setProfile(member_).enqueue(object : Callback<Member> {
                override fun onResponse(call: Call<Member>, response: Response<Member>) {
                    if (response.isSuccessful) { //통신이 성공적이라면
                        //생성 시 id는 0. 서버에 POST로 생성 후 부여 받는 id로 교체
                        member.id = response.body()!!.id
                        realConfirm()  //수정된 정보를 로컬에도 적용
                    }
                }
                override fun onFailure(call: Call<Member>, t: Throwable) {
                    Toast.makeText(App.context(), "서버 통신 에러", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun realConfirm() {
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