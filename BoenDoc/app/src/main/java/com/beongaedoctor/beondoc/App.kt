package com.beongaedoctor.beondoc

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.beongaedoctor.beondoc.App.Companion.gson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.conscrypt.Conscrypt
import java.security.Security

class App : Application() {

    init{
        instance = this
    }

    companion object {
        lateinit var prefs : Preference
        lateinit var gson : Gson

        var instance: App? = null
        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        prefs = Preference(applicationContext) // 다른 액티비티보다 먼저 생성되어야 데이터 전달 가능. onCreate보다 먼저 초기화 해야 함
        gson = GsonBuilder().create()
        super.onCreate()
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }

}


class Preference (context: Context) {
    private val prefs : SharedPreferences = context.getSharedPreferences("shared",MODE_PRIVATE)

    fun setMember(key: String, data:Member) {
        val memberInfo = gson.toJson(data, Member::class.java)
        prefs.edit().putString(key, memberInfo).apply()
    }

    fun getMember(key:String, default: String) : Member {
        val gsonMemberInfo = prefs.getString(key, default)
        return gson.fromJson(gsonMemberInfo, Member::class.java)
    }

    fun setString(key:String, value:String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key:String, default : String): String? {
        return prefs.getString(key, default)
    }
}

//val testMemberInfo = App.prefs.getString("memberInfo", "")
//출처: https://leveloper.tistory.com/133 [꾸준하게]

//user 정보 db에 저장.
//App.prefs.setMember("memberInfo", member)
//App.prefs.getMember("memberInfo", "")