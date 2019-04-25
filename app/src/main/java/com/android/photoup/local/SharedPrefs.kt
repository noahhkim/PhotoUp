package com.android.photoup.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

class SharedPrefs @Inject constructor(application: Application) {

    companion object {
        /**
         * The constant value of preference name.
         */
        private const val PREF_NAME = "kotlin-dbx-android"

        /**
         * The constant value of preference mode.
         */
        private const val PREF_MODE = Context.MODE_PRIVATE

        /**
         * The constant value of key for access token.
         */
        private const val KEY_DBX_ACCESS_TOKEN = "access-token"
    }

    private var preferences: SharedPreferences

    /**
     * Initializes SharedPreferences.
     */
    init {
        preferences = application.getSharedPreferences(
            PREF_NAME,
            PREF_MODE
        )

    }

    /**
     * Commits the changes.
     */
    private inline fun SharedPreferences.edit(
        operation:
            (SharedPreferences.Editor) -> Unit
    ) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    /**
     * The property of DropBox access token.
     */
    var dbxAccessToken: String?
        get() = preferences.getString(KEY_DBX_ACCESS_TOKEN, "")
        set(value) = preferences.edit{it.putString(KEY_DBX_ACCESS_TOKEN, value)}
}