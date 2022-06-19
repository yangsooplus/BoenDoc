package com.beongaedoctor.beondoc

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClass {
    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    //Spring 서버의 AWS 주소
    private const val BASE_URL = "http://34.236.164.138:8080/"

    // SingleTon
    fun getInstance(): Retrofit {
        if (instance == null) { //Retrofit 객체가 없으면 새로 생성
            val interceptor = HttpLoggingInterceptor() //개발 중 통신을 인터셉트하여 디버깅을 용이하게 하는 라이브러리
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY) //통신한 response의 body를 가져와 보여준다.

            val client = OkHttpClient.Builder() //OkHttp client 생성
                .addInterceptor(interceptor) //인터셉터를 연결
                .connectTimeout(5000L, TimeUnit.MILLISECONDS) //타임아웃 설정: 통신시작 5초 후 타임아웃
                .build()

            instance = Retrofit.Builder()
                .baseUrl(BASE_URL) //Spring 서버
                .addConverterFactory(GsonConverterFactory.create(gson)) //json 컨버터 연결
                .client(client) //위에서 생성한 client 연결
                .build()
        }

        return instance!! //이미 존재하거나 새로 생성한 Retrofit 객체를 반환
    }
}