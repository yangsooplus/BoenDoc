package com.beongaedoctor.beondoc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class OtherResult(val name:String, val severity:Int, val explanation:String)

class OtherResultAdapter(val context: Context, val ResultList:ArrayList<OtherResult>) : BaseAdapter() {
    override fun getCount(): Int {
        return ResultList.size
    }

    override fun getItem(position: Int): Any {
        return ResultList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, converView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.otherresultitem, null)
        val nameTV = view.findViewById<TextView>(R.id.diseasename)
        //val severityTV = view.findViewById<TextView>(R.id.severity)
        val explanTV = view.findViewById<TextView>(R.id.explanation)
        val data = ResultList[position]

        nameTV.text = data.name
        explanTV.text = data.explanation

        /*
                when (data.severity) {
            0 -> {severityTV.setBackgroundResource(R.drawable.rectemergency)
                severityTV.text = "응급"}
            1 -> {severityTV.setBackgroundResource(R.drawable.rectserious)
                severityTV.text = "중증"}
            2 -> {severityTV.setBackgroundResource(R.drawable.rectlight)
                severityTV.text = "경증"}
        }
        */


        return view
    }
}