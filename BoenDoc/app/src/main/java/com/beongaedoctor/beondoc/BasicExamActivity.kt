package com.beongaedoctor.beondoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.BaseAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.beongaedoctor.beondoc.databinding.ActivityBasicExamBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasicExamActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var BEbinding: ActivityBasicExamBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = BEbinding!!

    private var pageNum = 0
    private var answerList = Array(5) { "" }

    lateinit var member : Member


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BEbinding = ActivityBasicExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        member = intent.getSerializableExtra("member") as Member
        println(member)


        supportFragmentManager.beginTransaction()
            .add(R.id.framelayout, DrugFragment())
            .commit()

    }

    override fun onStart() {
        super.onStart()

        binding.nextBtn.setOnClickListener {
            getDataFromFragment()
            if (pageNum < 4) {
                pageNum++
                switchFragment()
            }
            else {
                setBasicExam()
                saveMember2Server(member)

                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }

        }

        binding.prevBtn.setOnClickListener {
            getDataFromFragment()
            if (pageNum > 0) {
                pageNum--
                switchFragment()
                if (pageNum == 0)
                    binding.prevBtn.isEnabled = false
            }
        }
    }


    private fun switchFragment() {


        val transaction = supportFragmentManager.beginTransaction()
        when (pageNum) {
            0 -> transaction.replace(R.id.framelayout, DrugFragment())
            1 -> transaction.replace(R.id.framelayout, SocialFragment())
            2 -> transaction.replace(R.id.framelayout, FamilyFragment())
            3 -> transaction.replace(R.id.framelayout, PhysicalFragment())
            4 -> transaction.replace(R.id.framelayout, WomanFragment())
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getDataFromFragment() {
        var fragment : Fragment

        when (pageNum) {
            0 -> {
                fragment = supportFragmentManager.findFragmentById(R.id.framelayout) as DrugFragment
                answerList[0] = fragment.view?.findViewById<EditText>(R.id.drugedittext)?.text.toString()
                println(answerList[0])
            }
            1 -> {
                fragment = supportFragmentManager.findFragmentById(R.id.framelayout) as SocialFragment
                answerList[1] = fragment.view?.findViewById<EditText>(R.id.socialedittext)?.text.toString()
                println(answerList[1])
            }
            2 -> {
                fragment = supportFragmentManager.findFragmentById(R.id.framelayout) as FamilyFragment
                answerList[2] = fragment.view?.findViewById<EditText>(R.id.familyedittext)?.text.toString()
                println(answerList[2])
            }
            3 -> {
                fragment = supportFragmentManager.findFragmentById(R.id.framelayout) as PhysicalFragment
                answerList[3] = fragment.view?.findViewById<EditText>(R.id.physicaledittext)?.text.toString()
                println(answerList[3])
            }
            4 -> {
                fragment = supportFragmentManager.findFragmentById(R.id.framelayout) as WomanFragment
                answerList[4] = fragment.view?.findViewById<EditText>(R.id.womanedittext)?.text.toString()
                println(answerList[4])
            }
        }
    }


    private fun setBasicExam()  {
        //var basicExam = BasicExam()

        member.drug = answerList[0]
        member.social = answerList[1]
        member.family = answerList[2]
        member.trauma = answerList[3]
        member.femininity = answerList[4]



    }

    private fun saveMember2Server(member: Member) {
        val retrofit = RetrofitClass.getInstance()
        val memberService = retrofit.create(MemberService::class.java)

        println(member)

        memberService.setProfile(member).enqueue(object : Callback<Member> {
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
    }

}