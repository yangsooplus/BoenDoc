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
        instance = this //App 클래스를 싱글톤 패턴으로 쓰기 위해 생성한 instance
    }

    companion object {
        lateinit var prefs : Preference //SharedPreference 사용을 위한 Preference 객체
        lateinit var gson : Gson //Json 파싱을 위한 Gson 라이브러리 객체

        var instance: App? = null //App 클래스 객체
        fun context() : Context { //App.context()를 호출하면 앱의 모든 곳에서 앱의 context를 사용할 수 있다
            return instance!!.applicationContext //context 반환
        }

        var noMember = false //비회원으로 기능 사용시 true.
    }

    override fun onCreate() {
        prefs = Preference(applicationContext) // 다른 액티비티보다 먼저 생성되어야 데이터 전달 가능. 타 액티비티의 onCreate보다 먼저 초기화 해야 함
        gson = GsonBuilder().create() //Gson 빌더를 빌드하여 바로 호출하여 사용할 수 있도록 함
        super.onCreate() //onCreate()로 App 클래스 생성
        Security.insertProviderAt(Conscrypt.newProvider(), 1) //OkHttp를 API 21 이하에서 사용하기 위해 작성
    }

}


//많은 곳에서 SharedPreference를 사용하기 때문에 싱글톤 패턴으로 설계했다.
class Preference (context: Context) { //SharedPreference 사용을 담당하는 클래스
    private val prefs : SharedPreferences = context.getSharedPreferences("shared",MODE_PRIVATE)

    //Member 클래스는 직렬화 되어있어서 String으로 저장하고 불러온다.

    //Member 클래스에 내용을 담아 저장한다.
    fun setMember(key: String, data:Member) {
        val memberInfo = gson.toJson(data, Member::class.java) //Member 클래스 객체를 Json으로 변환
        prefs.edit().putString(key, memberInfo).apply() //key와 매핑하여 생성된 Json을 저장한다.
    }

    //key로 Member 클래스 객제를 리턴한다.
    fun getMember(key:String, default: String) : Member {
        val gsonMemberInfo = prefs.getString(key, default) //key로 저장된 정보를 찾는다.
        if (gsonMemberInfo != null)  //저장된 정보를 가져오면 Json을 Member로 변환하여 리턴
            return gson.fromJson(gsonMemberInfo, Member::class.java)
        else //혹시라도 저장된 정보가 없다면
            return Member() //디폴트 객체를 반환한다.
    }

    //key에 해당하는 데이터를 삭제한다.
    fun deleteByKey(key: String) {
        prefs.edit().remove(key).apply()
    }

    //key-value 쌍으로 String 정보를 저장한다.
    fun setString(key:String, value:String) {
        prefs.edit().putString(key, value).apply()
    }

    //key로 저장된 String 정보를 불러온다
    fun getString(key:String, default : String): String? {
        return prefs.getString(key, default)
    }

    //key-value 쌍으로 Boolean 정보를 저장한다.
    fun setBool(key: String, value:Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    //key로 저장된 Boolean 정보를 불러온다
    fun getBool(key: String, default: Boolean) : Boolean {
        return prefs.getBoolean(key, default)
    }
}

//App을 싱글톤 클래스로 사용했기 때문에 아래형식으로 모든 클래스에서 사용이 가능하다.
//App.prefs.setMember("memberInfo", member)
//App.prefs.getMember("memberInfo", "")