package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.beongaedoctor.beondoc.databinding.ActivitySignInBinding
import com.beongaedoctor.beondoc.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    // 전역 변수로 바인딩 객체 선언
    private var SUABinding: ActivitySignUpBinding? = null

    // 매번 null 체크 하지 않도록 바인딩 변수 재선언
    private val binding get() = SUABinding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_up)

        SUABinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sexSpinner.adapter = ArrayAdapter.createFromResource(
        this, R.array.sexitemList, android.R.layout.simple_spinner_item
        )
        binding.sexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            //https://stickode.tistory.com/8
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


    }
}