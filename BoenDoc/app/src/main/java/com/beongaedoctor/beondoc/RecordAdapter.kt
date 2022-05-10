package com.beongaedoctor.beondoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DiagnosisViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    val dateNtime = v.findViewById<TextView>(R.id.recorddateNtime)
    val diseaseName = v.findViewById<TextView>(R.id.recorddisease)
}

class DiagnosisAdapter(val diagnosisList : DiagnosisList) : RecyclerView.Adapter<DiagnosisViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val content = LayoutInflater.from(parent.context).inflate(R.layout.recorditem, parent, false)
        return DiagnosisViewHolder(content)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.dateNtime.text = diagnosisList.diseaseList[position].diagnosisTime
        holder.diseaseName.text = diagnosisList.diseaseList[position].name

        holder.itemView.setOnClickListener { v ->
            //각 요소 누르면 실행
        }
    }

    override fun getItemCount(): Int {
        return diagnosisList.diseaseList.size
    }

}
