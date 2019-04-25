package com.android.photoup.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.android.photoup.data.Photo
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.WriteMode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.FileInputStream

class ProgressFragmentViewModel : ViewModel() {
    private val progressLiveData = MutableLiveData<Long>()
    private val subscriptions = CompositeDisposable()
    private val progressErrorLiveData = MutableLiveData<Throwable>()
    private val resultErrorLiveData = MutableLiveData<Throwable>()
    private val resultSuccessLiveData = MutableLiveData<FileMetadata>()

    /**
     * @return progress livedata
     */
    fun getProgressLiveData(): LiveData<Long> = progressLiveData

    /**
     * @return error livedata
     */
    fun getErrorLiveData(): LiveData<Throwable> = progressErrorLiveData

    /**
     * @return result error livedata
     */
    fun getResultErrorLiveData(): LiveData<Throwable> = resultErrorLiveData

    /**
     * @return result success livedata
     */
    fun getResultSuccessLiveData(): LiveData<FileMetadata> = resultSuccessLiveData

    /**
     * @param dbxClientV2 instance of dbx client needed to upload files to user's dbx account
     * @param photo object from livedata result
     *
     */
    fun setProgressLiveData(dbxClientV2: DbxClientV2, photo: Photo){
        subscriptions.add(Observable.just(FileInputStream(photo.imageFile))
            .map { inputStream ->
                dbxClientV2
                    .files()
                    .uploadBuilder("/" + photo.imageFile.name)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(inputStream) { long ->
                        Observable.just(long)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ val uploaded = 100 * it / photo.imageFile.length()
                                progressLiveData.value = uploaded },
                                { error -> progressErrorLiveData.value = error })
                    }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                resultSuccessLiveData.value = it
                Log.e("Success", it.pathDisplay)
            }
            ) { error ->
                resultErrorLiveData.value = error
                Log.e("Error", error.localizedMessage)
            })
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}

