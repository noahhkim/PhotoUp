package com.android.photoup.ui.viewmodel

import androidx.lifecycle.*
import com.android.photoup.data.Photo
import java.io.File

class MainActivityViewModel : ViewModel() {
    private val imagePathLiveData = MutableLiveData<Photo>()

    /**
     * @param path file path of the selected image
     */
    fun setUploadImageLiveData(path: String) {
        val file = Photo(File(path))
        imagePathLiveData.value = file
    }

    /**
     * @return path livedata object
     */
    fun getUploadImageLiveData(): LiveData<Photo> = imagePathLiveData
}

