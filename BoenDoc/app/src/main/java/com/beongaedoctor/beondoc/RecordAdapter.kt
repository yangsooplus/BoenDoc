package com.beongaedoctor.beondoc

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//RecyclerView의 Item View의 요소들 - 진단 날짜, 가장 확률이 높은 예상 질병명
class DiagnosisViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    val dateNtime = v.findViewById<TextView>(R.id.recorddateNtime)
    val diseaseName = v.findViewById<TextView>(R.id.recorddisease)
}

//RecylcerView에 List를 연결시켜주는 Adapter. 레이아웃과 데이터를 연결해주는 역할
class DiagnosisAdapter(val diagnosisList : ArrayList<List<Diagnosis>>) : RecyclerView.Adapter<DiagnosisViewHolder>() {
    //서버에서 진단 기록을 받아오는 동안 빈 recyclerview를 먼저 생성한다.
    //진단 기록을 받아오면 diagnosisList가 변경되었음을 알리고, UI를 갱신하도록 한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val content = LayoutInflater.from(parent.context).inflate(R.layout.recorditem, parent, false)
        return DiagnosisViewHolder(content)
    }

    //ViewHolder가 재활용될 때 사용되는 함수
    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        holder.dateNtime.text = diagnosisList[position][0].localDate //날짜
        holder.diseaseName.text = diagnosisList[position][0].diseaseName //질병명

        //진단 기록을 누르면 실행
        holder.itemView.setOnClickListener { v ->
            val context = v.context
            val resultIntent = Intent(context, DResultActivity::class.java) //진단 결과 페이지로 이동
            resultIntent.putExtra("diagnosisId", diagnosisList[position][0].id) //진단 기록 id를 intent로 전달한다.
            resultIntent.putExtra("fromChat", false) //진단 기록에서 진단 결과 페이지로 이동함을 알림
            context.startActivity(resultIntent)
        }
    }

    //진단 기록 리스트의 크기를 반환한다.
    override fun getItemCount(): Int {
        return diagnosisList?.size
    }

}
