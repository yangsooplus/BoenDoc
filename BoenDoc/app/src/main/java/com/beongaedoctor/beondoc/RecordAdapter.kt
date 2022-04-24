package com.beongaedoctor.beondoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Record(
    val dateNtime : String, //일시
    val diseaseName : String //질병명
)

class RecordViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    val dateNtime = v.findViewById<TextView>(R.id.recorddateNtime)
    val diseaseName = v.findViewById<TextView>(R.id.recorddisease)
}

class RecordAdapter(val recordList:ArrayList<Record>) : RecyclerView.Adapter<RecordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val content = LayoutInflater.from(parent.context).inflate(R.layout.recorditem, parent, false)
        return RecordViewHolder(content)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.dateNtime.text = recordList[position].dateNtime
        holder.diseaseName.text = recordList[position].diseaseName

        holder.itemView.setOnClickListener { v ->
            //각 요소 누르면 실행
        }
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

}