package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.beongaedoctor.beondoc.databinding.ActivityChatBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
    lateinit var dialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰바인딩
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = LoadingDialog(this)

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
            if (chatResposeList.size == 1) { //지금 들어가 있는 모델은 3개 인자가 따로따로.
                chatResposeList.add(sendText) //똑같은거 두 개 더 넣습니다
                chatResposeList.add(sendText)
            }


            if (chatItemTestList.size == 0) { //모든 질문에 응답하면 결과 창으로 이동. 여기서 통신해서 로딩하다가 넘어가도록 수정 해야 함.
                dialog.show()
                Predict(this)
            }
            else if (chatItemTestList.size > 0) { //질문 남아 있으면 제일 앞거 출력과 동시에 지워버리기
                chatItemList.add(chatItemTestList[0])
                chatItemTestList.removeAt(0)
            }

            (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(chatItemList.size - 1) //데이터 추가 알려줌. 리사이클러뷰 갱신
            binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1) //가장 밑으로 스크롤하기

            binding.chatEditText.setText("") //입력창 초기화


        }
    }


    private fun Predict(context: Context) {
        val gson = GsonBuilder().setLenient().create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5000L, TimeUnit.MILLISECONDS)
            .build()

        val retrofit_f = Retrofit.Builder()
            .baseUrl("http://192.168.200.127:5000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        val chatResponseService = retrofit_f.create(ChatResponseService::class.java)

        val test : List<String> = listOf("배가 아파요",
        "칼로 찢기는 듯한 통증, NRS 8점, 방사통 : 등으로 퍼짐",
            "구토, 속쓰림,[응급] 어지러움,갈증,소변량 감소,2회 흰 물만 나옴",
        "1일전",
        "명치 부위",
        "지속",
        "심해짐",
        "이전에도 3차례, 통증은 이번보다 약했음 ",
        "자세에 따른 변화 : 오른쪽으로 돌아 누우면 완화, 반듯이 누우면 악화",
        "없음",
        "술 : 1주일 6~7번, 하루 소주 2~3병, 담배 : 30갑년, 식사 : 매우 불규칙, 직업 : 포크레인 조종수",
        "없음",
        "없음",
        "없음",
        "없음")

        //원래: combinateChatResponse()
        //소화성궤양: ChatResponse(test)
        chatResponseService.sendResponse2Model(combinateChatResponse()).enqueue(object :
            Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    println("통신 성공: "+ (response.body()?.get(0)))
                    dialog.dismiss()
                    gotoResult(context, response.body()?.get(0))
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                println(t.message)
                dialog.dismiss()
                gotoResult(context, "서버 연결 에러")
            }

        })
    }

    private fun combinateChatResponse() : ChatResponse {
        val memberInfo = App.prefs.getMember("memberInfo", "")!!

        chatResposeList.add(memberInfo.drug)
        chatResposeList.add(memberInfo.social)
        chatResposeList.add(memberInfo.family)
        chatResposeList.add(memberInfo.trauma)
        chatResposeList.add(memberInfo.trauma)
        chatResposeList.add(memberInfo.femininity)

        return ChatResponse(chatResposeList)
    }

    private fun gotoResult(context: Context, result : String?) {
        val resultIntent = Intent(context, DResultActivity::class.java)
        resultIntent.putExtra("diseaseName1", result)
        resultIntent.putExtra("fromChat", true)
        startActivity(resultIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java) //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //인텐트 플래그 설정
        startActivity(intent) //인텐트 이동
        finish() //현재 액티비티 종료
    }
}