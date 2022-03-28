package com.beongaedoctor.beondoc

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

data class TestClass(
    var userId : Int,
    var id : Int,
    var title : String,
    var body : String
)


interface TestClassService {
    @GET("posts/1")
    fun getPost(): Call<TestClass>

    @GET("posts/{id}")
    fun getPostbyId(@Path("id") id: Int): Call<TestClass>
}