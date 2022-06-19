package com.beongaedoctor.beondoc

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView

class LoadingDialog(context: Context) : Dialog(context){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)

        val frameLoading : ImageView = findViewById(R.id.frameloading) //애니메이션이 들어갈 ImageView
        val frameAnimation : AnimationDrawable = frameLoading.background as AnimationDrawable //애니메이션 이미지가 담긴 파일

        frameLoading.post { frameAnimation.start() }

        // 취소 불가능. dismiss()하기 전까지 아무런 입력을 받지 않는다.
        setCancelable(false)

        // 배경 투명하게 바꿔줌
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
