package com.beongaedoctor.beondoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.GsonBuilder
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TestActivity : AppCompatActivity() {
    companion object {
        const val HKEY = "DFKY0ENCJZDVdxX4ulcZ8QSKuf1IYa6pG3TASfba0vk8Nv9DnW19C/nEftvSsZJAIwGWcSEXfP/pdXmNkKQJBQ=="
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        //xml 파싱
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(5000L, TimeUnit.MILLISECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B552657/HsptlAsembySearchService/")
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .client(client)
            .build()

        val hospitalAPI = retrofit.create(HospitalAPI::class.java)


        val x = 126.921018672032
        val y = 37.400673589522
/*
        hospitalAPI!!.getHospitalInfobyName(x, y,1, 1, HKEY).enqueue(object : Callback<Hospital>{
            override fun onResponse(call: Call<Hospital>, response: Response<Hospital>) {
                if (response.isSuccessful) {
                    println(response.body())
                }
            }

            override fun onFailure(call: Call<Hospital>, t: Throwable) {
                println(t.message)
            }

        })
*/

    }
}