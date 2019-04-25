package com.android.photoup.remote

import com.android.photoup.local.SharedPrefs
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult
import javax.inject.Inject

class DropboxClient @Inject constructor(sharedPrefs: SharedPrefs) {
    /**
     * Holds the DropBox API client.
     */
    private object ClientHolder {
        /**
         * A raw DropBox API client.
         */
        lateinit var clientV2: DbxClientV2

        /**
         * A boolean flag of whether client is initialized.
         */
        var initialized = false
    }

    /**
     * Returns whether this instance is initialized.
     */
    val initialized
        get() = ClientHolder.initialized

    init {
        val accessToken = sharedPrefs.dbxAccessToken
        if (!accessToken.isNullOrEmpty()) {
            initialize(accessToken)
        }
    }

    /**
     * Initializes this instance.
     * @param accessToken the access token for DropBox API.
     */
    fun initialize(accessToken: String) {
        val requestConfig = DbxRequestConfig("my-dbx-app")
        ClientHolder.clientV2 = DbxClientV2(requestConfig, accessToken)
        ClientHolder.initialized = true
    }
    /**
     * Retrieves Dropbox API client
     */
    fun getClient(): DbxClientV2 {
        return ClientHolder.clientV2
    }
}