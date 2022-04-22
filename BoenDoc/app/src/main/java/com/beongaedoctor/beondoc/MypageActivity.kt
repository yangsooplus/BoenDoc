package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.databinding.ActivityChatBinding
import com.beongaedoctor.beondoc.databinding.ActivityMainBinding
import com.beongaedoctor.beondoc.databinding.ActivityMypageBinding
import com.google.gson.GsonBuilder

class MypageActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var mpbinding: ActivityMypageBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = mpbinding!!

    val RecordList = arrayListOf(
        Record("2022-04-21", "예시질병1"),
        Record("2022-04-22", "예시질병2"),
        Record("2022-04-23", "예시질병3")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mpbinding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sp = getSharedPreferences("shared", MODE_PRIVATE)
        val gson = GsonBuilder().create()

        if (sp != null) {
            val gsonMemberInfo = sp!!.getString("memberInfo","")
            val testMemberInfo : Member = gson!!.fromJson(gsonMemberInfo, Member::class.java)

            binding.username.text = testMemberInfo.name

            //binding.useremail.text = testMemberInfo. //이메일 추가 해야함

            println(testMemberInfo.gender)
            if (testMemberInfo.gender == 0L)
                binding.usersex.text = "여"
            else
                binding.usersex.text = "남"

            binding.userage.text = testMemberInfo.age
            binding.userheight.text = testMemberInfo.height
            binding.userweight.text = testMemberInfo.weight
        }

        binding.mypageRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mypageRecyclerView.adapter = RecordAdapter(RecordList)

    }
}