package com.android.photoup.di.module

import android.app.Application
import com.android.photoup.App
import com.android.photoup.local.SharedPrefs
import com.android.photoup.remote.DropboxClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: App) {

    @Provides
    @Singleton
    fun provideApp(): Application = app


    @Provides
    @Singleton
    fun provideSharedPrefs(application: Application) : SharedPrefs =
        SharedPrefs(application)

    @Provides
    @Singleton
    fun provideDropboxClient(sharedPrefs: SharedPrefs) : DropboxClient =
            DropboxClient(sharedPrefs)
}