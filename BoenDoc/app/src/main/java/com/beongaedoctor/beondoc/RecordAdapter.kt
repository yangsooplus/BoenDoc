package com.beongaedoctor.beondoc

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.beongaedoctor.beondoc.App.Companion.context


class DiagnosisViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    val dateNtime = v.findViewById<TextView>(R.id.recorddateNtime)
    val diseaseName = v.findViewById<TextView>(R.id.recorddisease)
}

class DiagnosisAdapter(val diagnosisList : ArrayList<List<Diagnosis>>) : RecyclerView.Adapter<DiagnosisViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val content = LayoutInflater.from(parent.context).inflate(R.layout.recorditem, parent, false)

        return DiagnosisViewHolder(content)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        if (diagnosisList != null) {
            holder.dateNtime.text = diagnosisList[position][0].localDate.toString()
        }
        if (diagnosisList != null) {
            holder.diseaseName.text = diagnosisList[position][0].diseaseName
        }

        holder.itemView.setOnClickListener { v ->
            //각 요소 누르면 실행
            val context = v.context
            val resultIntent = Intent(context, DResultActivity::class.java)
            if (diagnosisList != null) {
                resultIntent.putExtra("diagnosisId", diagnosisList[position][0].id)
            }
            resultIntent.putExtra("fromChat", false)
            context.startActivity(resultIntent)
        }
    }

    override fun getItemCount(): Int {
        return diagnosisList?.size ?: 0
    }

}
