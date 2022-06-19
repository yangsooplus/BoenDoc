package com.beongaedoctor.beondoc

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

    // RecyclerView에 표시될 ChatItem을 담을 리스트
    private val chatItemList = arrayListOf<ChatItem>()

    // 번개닥터 질문이 들어갈 리스트. 회원/비회원에 따라 아래의 MemberchatItemList/NoMemverchatItemList를 연결한다.
    lateinit var chatItemTestList : ArrayList<ChatItem>

    // 회원일 시 나오는 질문
    private val MemberchatItemList = arrayListOf(
        ChatItem(ChatItem.TYPE_LEFT, "다른 증상도 같이 나타난다면 알려주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 있는 부위가 어디인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상의 특징을 더 자세히 설명해주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "언제부터 증상이 나타났나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 얼마나 지속되었나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상의 양상이 어떤가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "이전에도 같은 증상을 경험한 적이 있나요? 혹은 처음 겪는 증상인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 괜찮아지는 상황이 있다면 얘기해주세요.")
    )

    // 비회원일 시 나오는 질문. 기초문진 내용을 포함한다.
    private val NoMemberchatItemList = arrayListOf(
        ChatItem(ChatItem.TYPE_LEFT, "다른 증상도 같이 나타난다면 알려주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 있는 부위가 어디인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상의 특징을 더 자세히 설명해주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "언제부터 증상이 나타났나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 얼마나 지속되었나요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상의 양상이 어떤가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "이전에도 같은 증상을 경험한 적이 있나요? 혹은 처음 겪는 증상인가요?"),
        ChatItem(ChatItem.TYPE_LEFT, "증상이 괜찮아지는 상황이 있다면 얘기해주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "복용하시는 약물이 있으면 얘기해주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "술, 담배, 커피, 직업 등 사회력을 알려주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "가족력이 있다면 얘기해주세요"),
        ChatItem(ChatItem.TYPE_LEFT, "과거 앓았던 질병이나 수술 경험이 있다면 알려주세요."),
        ChatItem(ChatItem.TYPE_LEFT, "신장을 입력해주세요. 숫자만 입력해주세요. ex)170"),
        ChatItem(ChatItem.TYPE_LEFT, "체중을 입력해주세요. 숫자만 입력해주세요. ex)70")
    )

    // 사용자의 응답 리스트
    private var chatResposeList = arrayListOf<String>()

    lateinit var dialog : LoadingDialog //로딩 창

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //뷰바인딩
        chatbinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = LoadingDialog(this) //로딩창

        //회원 여부에 따라 질문 리스트를 선택한다.
        chatItemTestList = if (App.noMember)
            NoMemberchatItemList
        else
            MemberchatItemList

        //수직 드래그가 가능하게 LinearLayoutManager를 연결한다.
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        //어댑터 연결. chatItemList의 내용이 리사이클러뷰에 표시된다.
        binding.chatRecyclerView.adapter = ChatAdapter(chatItemList)

        //초기 답변이 출력된다.
        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "안녕하세요 번개닥터입니다."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(0) //메세지 넣고 어댑터에 갱신 신호
        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "질문에 해당사항이 없을 경우엔 아무 내용 없이 전송 버튼을 눌러주세요."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(1) //메세지 넣고 어댑터에 갱신 신호
        chatItemList.add(ChatItem(ChatItem.TYPE_LEFT, "현재 겪고 계신 증상이 어떤가요? 발생하는 증상을 자세히 설명해주세요."))
        (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(2) //메세지 넣고 어댑터에 갱신 신호


        //채팅 전달 버튼
        binding.chatTransmit.setOnClickListener {
            val sendText : String = binding.chatEditText.text.toString() //보낼 텍스트
            chatItemList.add(ChatItem(ChatItem.TYPE_RIGHT, sendText)) //string 담아서 채팅 아이템 추가
            chatResposeList.add(sendText) //응답 데이터로 사용할 string 담기

            if (chatItemTestList.size == 0) { //모든 질문에 응답하면 결과 창으로 이동
                dialog.show() //로딩 창 출력
                Predict(this, combinateChatResponse()) // 예측 모델에 응답을 합성하여 전달
            }
            else if (chatItemTestList.size > 0) { //질문 남아 있으면 제일 앞 질문 출력과 동시에 지워버리기
                chatItemList.add(chatItemTestList[0])
                chatItemTestList.removeAt(0)
            }

            (binding.chatRecyclerView.adapter as ChatAdapter).notifyItemInserted(chatItemList.size - 1) //데이터 추가 알려줌. 리사이클러뷰 갱신
            binding.chatRecyclerView.scrollToPosition(chatItemList.size - 1) //가장 밑으로 스크롤하기
            binding.chatEditText.setText("") //입력창 초기화
        }
    }

    //Flask서버의 예측 모델에게 사용자의 응답을 넘겨주고 예측 값을 받아오는 함수
    private fun Predict(context: Context, chatResponse: String) {
        //---------Http 통신을 위한 과정 (설명 생략)---------
        //싱글톤으로 사용하는 객체는 스프링 서버 주소를 Base Url로 한다.
        // Flask와 통신할 Retrofit 객체를 새로 생성한다.
        val gson = GsonBuilder().setLenient().create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5000L, TimeUnit.MILLISECONDS)
            .build()

        val retrofit_f = Retrofit.Builder()
            .baseUrl("http://192.168.114.226:5000/") //Flask 서버가 AWS 배포하기에 너무 커서 로컬 서버로 실행
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        // 인터페이스로 서비스 생성
        val chatResponseService = retrofit_f.create(ChatResponseService::class.java)

        // 사용자의 채팅 응답 <-> 예측 결과
        chatResponseService.sendResponse2Model(ChatResponse(chatResponse)).enqueue(object :
            Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) { //통신 성공 시
                if (response.isSuccessful) {
                    gotoResult(context, response.body()!!) //예측 결과를 담아서 결과 창으로 이동한다.
                }
                else //통신이 정상적으로 이루어지지 않을 시
                    Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                dialog.dismiss() //로딩 창 종료
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) { //통신 실패 시
                dialog.dismiss() //로딩 창 종료
                Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        })
    }

    //사용자의 응답을 모델에서 사용하는 형식으로 합성
    private fun combinateChatResponse() : String {
        var responseStr = ""

        if (App.noMember) { //비회원의 경우
            var obesity: String

            //채팅으로 입력한 키와 몸무게로 비만도를 계산한다.
            val bmi = chatResposeList[14].toDouble()/(chatResposeList[13].toDouble()/100)*(chatResposeList[13].toDouble()/100)
            if (bmi < 0.0) obesity = "알 수 없음"
            else if (bmi < 20.0)  obesity = "저체중"
            else if (bmi <= 24.0)  obesity = "정상"
            else if (bmi <= 29.0)  obesity = "과체중"
            else obesity = "비만"

            //형식에 맞게 하나의 string으로 합친다.
            responseStr = "${chatResposeList[4]} ${chatResposeList[7]} ${chatResposeList[9]} ${chatResposeList[10]} " +
                    "${chatResposeList[11]} ${chatResposeList[12]} ${chatResposeList[2]} " +
                    "  ${chatResposeList[5]} ${chatResposeList[6]} ${chatResposeList[0]} " +
                    "${chatResposeList[1]} ${chatResposeList[3]} ${chatResposeList[8]} $obesity"

        }
        else { //회원의 경우
            val memberInfo = App.prefs.getMember("memberInfo", "") //회원 정보를 불러온다.
            var obesity: String

            //회원 정보의 키와 몸무게로 비만도를 계산한다.
            val bmi = memberInfo.weight.toDouble()/(memberInfo.height.toDouble()/100)*(memberInfo.height.toDouble()/100)
            if (bmi < 0.0) obesity = "알 수 없음"
            else if (bmi < 20.0)  obesity = "저체중"
            else if (bmi <= 24.0)  obesity = "정상"
            else if (bmi <= 29.0)  obesity = "과체중"
            else obesity = "비만"

            //형식에 맞게 하나의 string으로 합친다.
            responseStr = "${chatResposeList[4]} ${chatResposeList[7]} ${memberInfo.drug} ${memberInfo.social} " +
                    "${memberInfo.family} ${memberInfo.trauma} ${chatResposeList[2]} " +
                    "  ${chatResposeList[5]} ${chatResposeList[6]} ${chatResposeList[0]} " +
                    "${chatResposeList[1]} ${chatResposeList[3]} ${chatResposeList[8]} $obesity"
        }
        return responseStr
    }

    //예측 결과를 가지고 진단 결과 창으로 이동한다.
    private fun gotoResult(context: Context, result : List<String>) {
        val resultIntent = Intent(context, DResultActivity::class.java)

        //3개의 질병 이름과 확률을 intent로 전달한다.
        resultIntent.putExtra("diseaseName1", result[0])
        resultIntent.putExtra("diseaseName2", result[1])
        resultIntent.putExtra("diseaseName3", result[2])
        resultIntent.putExtra("diseaseProb1", result[3])
        resultIntent.putExtra("diseaseProb2", result[4])
        resultIntent.putExtra("diseaseProb3", result[5])
        resultIntent.putExtra("fromChat", true) // (채팅 -> 진단 결과)의 루트로 이동했다는 것을 알림
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