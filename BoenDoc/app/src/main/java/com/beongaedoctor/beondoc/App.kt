package com.beongaedoctor.beondoc

import android.app.Application
import android.content.Context
import org.conscrypt.Conscrypt
import java.security.Security

class App : Application() {

    init{
        instance = this
    }

    companion object {
        var instance: App? = null
        fun context() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }

}