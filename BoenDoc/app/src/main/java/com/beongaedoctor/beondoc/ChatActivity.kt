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

    private val chatItemList = arrayListOf<ChatItem>()
    private val chatItemTestList = arrayListOf(

        ChatItem(ChatItem.TYPE_LEFT, "언제부터 증상이 나타났나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 있는 부위가 어디인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 얼마나 지속되었나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상의 양상이 어떤가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "이전에도 같은 증상을 경험한 적이 있나요? 혹은 처음 겪는 증상인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 괜찮아지는 상황이 있다면 얘기해주세요."),

    )
    private var chatResposeList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰바인딩
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //리사이클러뷰 수직 드래그 가능
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        //어댑터 연결
        binding.chatRecyclerView.adapter = ChatAdapter(chatItemList)


        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "안녕하세요 번개닥터입니다."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(0) //메세지 넣고 어댑터에 갱신 신호
        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "현재 겪고 계신 증상이 어떤가요? 발생하는 모든 증상과 특성을 자세히 설명해주세요."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(1) //메세지 넣고 어댑터에 갱신 신호


        //채팅 전달 버튼
        binding.chatTransmit.setOnClickListener {
            val sendText : String = binding.chatEditText.text.toString() //보낼 텍스트
            chatItemList.add(ChatItem(ChatItem.TYPE_RIGHT, sendText)) //string 담아서 채팅 아이템 추가
            chatResposeList.add(sendText) //응답 데이터로 사용할 string 담기

            if (chatItemTestList.size > 0) { //질문 남아 있으면 제일 앞거 출력과 동시에 지워버리기
                chatItemList.add(chatItemTestList[0])
                chatItemTestList.removeAt(0)
            }

            (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(chatItemList.size - 1) //데이터 추가 알려줌. 리사이클러뷰 갱신
            binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1) //가장 밑으로 스크롤하기

            binding.chatEditText.setText("") //입력창 초기화

            if (chatItemTestList.size == 0) { //모든 질문에 응답하면 결과 창으로 이동. 여기서 통신해서 로딩하다가 넘어가도록 수정 해야 함.
                val resultIntent = Intent(this, ResultActivity::class.java)
                startActivity(resultIntent)
            }
        }
    }
}