package com.android.photoup.di.component

import com.android.photoup.App
import com.android.photoup.di.module.AppModule
import com.android.photoup.local.SharedPrefs
import com.android.photoup.remote.DropboxClient
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class]
)
interface AppComponent {
    fun inject(app: App)
    fun sharedPrefs() : SharedPrefs
    fun dropboxClient() : DropboxClient
}