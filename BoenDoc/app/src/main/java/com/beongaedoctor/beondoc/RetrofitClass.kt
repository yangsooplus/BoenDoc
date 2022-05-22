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



    // 서버 주소
    //private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    // 달콤 주소
    //private const val BASE_URL = "http://172.30.1.18:8080/"

    //AWS 주소
    private const val BASE_URL = "http://34.236.164.138:8080/"

    // SingleTon
    fun getInstance(): Retrofit {
        if (instance == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .build()

            instance = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }

        return instance!!
    }
}