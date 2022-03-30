package com.beongaedoctor.beondoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatItem(val content:String)

class ChatViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val content = v.findViewById<TextView>(R.id.leftChatItem)
}

class ChatAdapter(val ChatItemList:ArrayList<ChatItem>) : RecyclerView.Adapter<ChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //리사이클러뷰 생성
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.leftchatitem, parent, false)
        return ChatViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        //각 아이템에 정보 넣기
        holder.content.text = ChatItemList[position].content

        //각 아이템별 클릭 액션은 이곳에
        //holder.itemView.setOnClickListener {  }
    }

    override fun getItemCount(): Int {
        return ChatItemList.size
    }
}