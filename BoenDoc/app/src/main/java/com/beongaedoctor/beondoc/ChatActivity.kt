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
        ChatItem(ChatItem.TYPE_LEFT,"언제부터 아팠나요?"),
        ChatItem(ChatItem.TYPE_LEFT,"증상이 있는 부위가 어디인가요?"),
        ChatItem(ChatItem.TYPE_LEFT,"증상이 얼마나 지속되었나요?"),
        ChatItem(ChatItem.TYPE_LEFT,"증상의 양상이 어떤가요?"),
        ChatItem(ChatItem.TYPE_LEFT,"이전에도 같은 증상을 경험한 적이 있나요?"),
        ChatItem(ChatItem.TYPE_LEFT,"증상을 더 자세히 설명해주세요."),
        ChatItem(ChatItem.TYPE_LEFT,"복용하고 계신 약물이 있다면 얘기해주세요.")
    )
    var chatResposeList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_chat)
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //리사이클러뷰 수직 드래그 가능
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        //어댑터 연결
        binding.chatRecyclerView.adapter = ChatAdapter(chatItemList)


        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "안녕하세요 번개닥터입니다."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(0)
        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "현재 겪고 계신 주요 증상을 설명해주세요."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(1)



        binding.chatTransmit.setOnClickListener {
            val sendText : String = binding.chatEditText.text.toString()
            chatItemList.add(ChatItem(ChatItem.TYPE_RIGHT, sendText))
            chatResposeList.add(sendText)

            if (chatItemTestList.size > 0) { //테스트용 더미 텍스트
                chatItemList.add(chatItemTestList[0])
                chatItemTestList.removeAt(0)
            }

            (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(chatItemList.size - 1) //데이터 추가 알려줌. 리사이클러뷰 갱신
            binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1) //가장 밑으로 스크롤하기

            binding.chatEditText.setText("")
        }

        binding.tempBtn.setOnClickListener {
            println(chatResposeList)

            //나중에는 스레드나.. 콜백 함수 이용해서 진단 완료 시 자동으로 액티비티 이동
            val resultIntent = Intent(this, ResultActivity::class.java)
            startActivity(resultIntent)
        }


    }


}