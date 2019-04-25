package com.android.photoup.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.photoup.App
import com.android.photoup.BuildConfig
import com.android.photoup.R
import com.android.photoup.local.SharedPrefs
import com.android.photoup.remote.DropboxClient
import com.android.photoup.ui.viewmodel.MainActivityViewModel
import com.bumptech.glide.Glide
import com.dropbox.core.android.Auth
import kotlinx.android.synthetic.main.fragment_preview.*
import kotlinx.android.synthetic.main.fragment_preview.view.*
import java.io.File

class PreviewFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPrefs
    private lateinit var dropboxClient: DropboxClient
    private lateinit var mainActivityViewModel: MainActivityViewModel

    companion object {
        fun newInstance() = PreviewFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_preview, container, false)

        // Initialize sharedPreferences dropboxClient
        initDependencies()

        // Set OnClickListeners for cancel and upload buttons
        view.cancelButton.setOnClickListener { activity?.supportFragmentManager?.popBackStack() }
        view.uploadPhotoButton.setOnClickListener {
            if (!dropboxClient.initialized) {
                // if dbxclient is not initialized, start auth process
                Auth.startOAuth2Authentication(activity, BuildConfig.DROPBOX_KEY)
            } else {
                initProgressFragment()
            }
        }
        return view
    }

    private fun initDependencies() {
        val component = App.instance.getAppComponent()
        sharedPrefs = component.sharedPrefs()
        dropboxClient = component.dropboxClient()
    }

    private fun initProgressFragment() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.container,
                ProgressFragment.newInstance(), ProgressFragment::class.java.name
            )
            ?.addToBackStack(null)
            ?.commit()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { mainActivityViewModel = ViewModelProviders.of(it).get(MainActivityViewModel::class.java) }
        observeViewModel(mainActivityViewModel)
    }

    private fun observeViewModel(mainActivityViewModel: MainActivityViewModel) {
        mainActivityViewModel.getUploadImageLiveData().observe(this,
            Observer {
                loadImagePreview(it.imageFile)
            })
    }

    /**
     * @param file The File containing the image
     */
    private fun loadImagePreview(file: File) {
        Glide.with(this)
            .asBitmap()
            .load(file)
            .into(selectedImage)
    }

    override fun onResume() {
        super.onResume()
        //check authentication
        val accessToken = getAccessToken()
        if (accessToken.isNotEmpty()) {
            dropboxClient.initialize(accessToken)
        }
    }

    /**
     * @return Retrieve access token from SharedPrefs
     * if access token does not exist, retrieve it from Auth helper class and store in SharedPrefs
     *
     */
    private fun getAccessToken(): String {
        var accessToken = sharedPrefs.dbxAccessToken
        accessToken?.let {
            if (it.isEmpty()) {
                accessToken = Auth.getOAuth2Token()
                if (accessToken.isNullOrEmpty()) {
                    accessToken = ""
                } else {
                    sharedPrefs.dbxAccessToken = accessToken
                }
            }
        }
        return accessToken!!
    }
}
