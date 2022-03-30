package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.databinding.ActivityChatBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding

class ChatActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var chatbinding: ActivityChatBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = chatbinding!!

    val chatItemTestList = arrayListOf(
        ChatItem("어쩌구저쩌구"),
        ChatItem("더미 데이터입니다"),
        ChatItem("글자 수가 늘어나면 말풍선도 따라서 커집니다. 가로 최대 길이는 240dp 입니다."),
        ChatItem("글자 수가 적으면 그만큼 풍선을 작게 만들고 싶은데 그렇게 하면 간단하게 줄바꿈이 안되더라고요"),
        ChatItem("일단 기능적인 부분부터 구현한 다음에 수정하려고 합니다"),
        ChatItem("어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //리사이클러뷰 수직 드래그 가능
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        //어댑터 연결
        binding.chatRecyclerView.adapter = ChatAdapter(chatItemTestList)

    }
}