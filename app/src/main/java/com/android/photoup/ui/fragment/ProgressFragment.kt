package com.android.photoup.ui.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.photoup.App

import com.android.photoup.R
import com.android.photoup.local.SharedPrefs
import com.android.photoup.remote.DropboxClient
import com.android.photoup.ui.viewmodel.MainActivityViewModel
import com.android.photoup.ui.viewmodel.ProgressFragmentViewModel
import kotlinx.android.synthetic.main.fragment_progress.*
import kotlinx.android.synthetic.main.fragment_progress.view.*

class ProgressFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPrefs
    private lateinit var dropboxClient: DropboxClient
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var progressFragmentViewModel: ProgressFragmentViewModel

    companion object {
        fun newInstance() = ProgressFragment()
        const val PROGRESS_MAX = 100
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_progress, container, false)

        initDependencies()

        // init progress VM
        progressFragmentViewModel = ViewModelProviders.of(this).get(ProgressFragmentViewModel::class.java)
        observeProgressVM(progressFragmentViewModel)


        view.selectMorePhotos.setOnClickListener { returnToSelectFragment() }

        return view
    }

    private fun initDependencies() {
        val component = App.instance.getAppComponent()
        sharedPrefs = component.sharedPrefs()
        dropboxClient = component.dropboxClient()
    }

    private fun returnToSelectFragment() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(
                R.id.container,
                SelectFragment.newInstance(), SelectFragment::class.java.name
            )
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { mainActivityViewModel = ViewModelProviders.of(it).get(MainActivityViewModel::class.java) }
        observeMainVM(mainActivityViewModel)
    }

    /**
     * @param progressVM update progress bar and views by observing this VM
     */
    private fun observeProgressVM(progressVM: ProgressFragmentViewModel) {
        progressVM.getProgressLiveData().observe(this, Observer {
            Log.e("long progress", it.toString())
            displayProgress(it)
        })
        progressVM.getErrorLiveData().observe(this, Observer {
            switchToErrorView(it)
        })
        progressVM.getResultErrorLiveData().observe(this, Observer {
            switchToErrorView(it)
        })
        progressVM.getResultSuccessLiveData().observe(this, Observer {
            switchToSuccessView()
        })
    }

    /**
     * @param mainActivityViewModel observe this VM to set livedata object for progressFrag VM
     */
    private fun observeMainVM(mainActivityViewModel: MainActivityViewModel) {
        mainActivityViewModel.getUploadImageLiveData().observe(this, Observer {
            progressFragmentViewModel.setProgressLiveData(dropboxClient.getClient(), it)
        })
    }


    /**
     * @param bytes The file uploaded progress
     *
     */
    private fun displayProgress(bytes: Long) {
        view?.progressBar?.max = PROGRESS_MAX
        view?.progressBar?.progress = bytes.toInt()
    }

    private fun switchToSuccessView() {
        removeProgressView()
        successLayout.visibility = View.VISIBLE
    }

    private fun switchToErrorView(error: Throwable) {
        removeProgressView()
        errorLayout.visibility = View.VISIBLE
        errorMessage.text = error.localizedMessage
    }

    private fun removeProgressView() {
        selectMorePhotos.visibility = View.VISIBLE
        progressLayout.visibility = View.GONE
    }
}
