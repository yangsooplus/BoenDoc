package com.beongaedoctor.beondoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException


class ChatItem(val type: Int, val content:String) {
    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
    }
}

//class ChatViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//    val content = v.findViewById<TextView>(R.id.leftChatItem)
//}



class ChatAdapter(val ChatItemList:ArrayList<ChatItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        return when (viewType) {
            ChatItem.TYPE_LEFT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.leftchatitem, parent, false)
                leftChatViewHolder(view)
            }
            ChatItem.TYPE_RIGHT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.rightchatitem, parent, false)
                rightChatViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입")

        }

        //리사이클러뷰 생성
        //val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.leftchatitem, parent, false)
        //return ChatViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = ChatItemList[position]

        when(item.type) {
            ChatItem.TYPE_LEFT -> (holder as leftChatViewHolder).content.text = item.content
            ChatItem.TYPE_RIGHT -> (holder as rightChatViewHolder).content.text = item.content
        }




        //각 아이템별 클릭 액션은 이곳에
        //holder.itemView.setOnClickListener {  }
    }

    override fun getItemCount(): Int {
        return ChatItemList.size
    }

    override fun getItemViewType(position: Int): Int {

        return ChatItemList[position].type
    }



    inner class leftChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.leftChatItem)
    }

    inner class rightChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.rightChatItem)
    }

}