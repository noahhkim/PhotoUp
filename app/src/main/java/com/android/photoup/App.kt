package com.android.photoup

import android.app.Activity
import android.app.Application
import com.android.photoup.di.component.AppComponent
import com.android.photoup.di.component.DaggerAppComponent
import com.android.photoup.di.module.AppModule
import com.android.photoup.remote.DropboxClient
import dagger.Component
import javax.inject.Inject

class App : Application() {
    lateinit var component: AppComponent

    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()

        instance = this

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        component.inject(this)
    }

    fun getAppComponent(): AppComponent {
        return component
    }

    companion object {
        lateinit var instance: App private set
    }
}