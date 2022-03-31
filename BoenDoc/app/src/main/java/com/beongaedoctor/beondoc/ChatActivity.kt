package com.beongaedoctor.beondoc

import android.content.Intent
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

    val chatItemList = arrayListOf<ChatItem>()
    val chatItemTestList = arrayListOf(
        ChatItem(ChatItem.TYPE_LEFT, "어쩌구저쩌구"),
        ChatItem(ChatItem.TYPE_LEFT,"더미 데이터입니다"),
        ChatItem(ChatItem.TYPE_LEFT,"글자 수가 늘어나면 말풍선도 따라서 커집니다. 가로 최대 길이는 240dp 입니다."),
        ChatItem(ChatItem.TYPE_LEFT,"글자 수가 적으면 그만큼 풍선을 작게 만들고 싶은데 그렇게 하면 간단하게 줄바꿈이 안되더라고요"),
        ChatItem(ChatItem.TYPE_LEFT,"일단 기능적인 부분부터 구현한 다음에 수정하려고 합니다"),
        ChatItem(ChatItem.TYPE_LEFT, ("어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구어쩌구저쩌구")))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //리사이클러뷰 수직 드래그 가능
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        //어댑터 연결
        binding.chatRecyclerView.adapter = ChatAdapter(chatItemList)


        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "안녕하세요 번개닥터입니다."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(0)


        binding.chatTransmit.setOnClickListener {
            val sendText : String = binding.chatEditText.text.toString()
            chatItemList.add(ChatItem(ChatItem.TYPE_RIGHT, sendText))
            if (chatItemTestList.size > 0) { //테스트용 더미 텍스트
                chatItemList.add(chatItemTestList[0])
                chatItemTestList.removeAt(0)
            }

            (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(chatItemList.size - 1) //데이터 추가 알려줌. 리사이클러뷰 갱신
            binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1) //가장 밑으로 스크롤하기

            binding.chatEditText.setText("")
        }

        binding.tempBtn.setOnClickListener {
            //나중에는 스레드나.. 함수 이용해서 진단 완료 시 자동으로 액티비티 이동
            val resultIntent = Intent(this, ResultActivity::class.java)
            startActivity(resultIntent)
        }


    }


}