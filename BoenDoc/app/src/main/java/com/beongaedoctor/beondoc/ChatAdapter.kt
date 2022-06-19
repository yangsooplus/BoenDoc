package com.beongaedoctor.beondoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException


class ChatItem(val type: Int, val content:String) { //type: 좌측 0/우측 1, content: 말풍선 속 내용
    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
    }
}

//RecylcerView에 List를 연결시켜주는 Adapter. 레이아웃과 데이터를 연결해주는 역할
class ChatAdapter(val ChatItemList:ArrayList<ChatItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        return when (viewType) {
            ChatItem.TYPE_LEFT -> { //좌측 레이아웃 생성
                view = LayoutInflater.from(parent.context).inflate(R.layout.leftchatitem, parent, false)
                leftChatViewHolder(view)
            }
            ChatItem.TYPE_RIGHT -> { //우측 레이아웃 생성
                view = LayoutInflater.from(parent.context).inflate(R.layout.rightchatitem, parent, false)
                rightChatViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입") //다른 값이 들어온다면 콘솔에 메세지 출력
        }
    }

    //ViewHolder가 재활용될 때 사용되는 함수
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = ChatItemList[position]

        when(item.type) { //ChatItem type에 맞게 말풍선 내용을 채운다.
            ChatItem.TYPE_LEFT -> (holder as leftChatViewHolder).content.text = item.content
            ChatItem.TYPE_RIGHT -> (holder as rightChatViewHolder).content.text = item.content
        }
    }

    //채팅 데이터 리스트의 크기를 반환한다.
    override fun getItemCount(): Int {
        return ChatItemList.size
    }

    //채팅 데이터 리스트의 position번째에 있는 데이터의 ViewType을 반환한다.
    override fun getItemViewType(position: Int): Int {
        return ChatItemList[position].type
    }

    //아이템 뷰를 저장하는 inner class. 말풍선에서 필요한 view는 말풍선 안 TextView뿐 (좌측 레이아웃)
    inner class leftChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.leftChatItem)
    }

    //아이템 뷰를 저장하는 inner class. 말풍선에서 필요한 view는 말풍선 안 TextView뿐 (우측 레이아웃)
    inner class rightChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.rightChatItem)
    }
}