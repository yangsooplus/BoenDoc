package com.beongaedoctor.beondoc

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

class CustomDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: ButtonClickListener

    fun showCustomDialog(showText : String) {
        dialog.setContentView(R.layout.dialog_custom)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)


        val textView = dialog.findViewById<TextView>(R.id.dialogText)
        val yesBtn = dialog.findViewById<Button>(R.id.yesBtn)
        val noBtn = dialog.findViewById<Button>(R.id.noBtn)

        textView.text = showText

        yesBtn.setOnClickListener {
            onClickListener.onClicked(true)
            dialog.dismiss()
        }

        noBtn.setOnClickListener {
            onClickListener.onClicked(false)
            dialog.dismiss()
        }


        dialog.show()
    }

    interface ButtonClickListener{
        fun onClicked(boolean: Boolean)
    }

    fun setOnClickListener(listener: ButtonClickListener) {
        onClickListener = listener
    }
}