package com.android.photoup.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.photoup.R
import com.android.photoup.ui.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_select.view.*


class SelectFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    companion object {
        fun newInstance() = SelectFragment()
        const val GALLERY_REQUEST_CODE = 1000
        const val PERMISSION_CODE = 1001
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_select, container, false)

        view.selectPhotoButton.setOnClickListener {
            checkPhotosPermission()
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { mainActivityViewModel = ViewModelProviders.of(it).get(MainActivityViewModel::class.java) }
    }

    private fun checkPhotosPermission() {
        if (context?.let { ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) }
            != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            }
        } else {
            pickFromGallery()
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,
            GALLERY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    // Set uri to livedata in SelectViewModel
                    val uri = data?.data
                    val pathString = uri?.let { getPathFromUri(it) }
                    pathString?.let { mainActivityViewModel.setUploadImageLiveData(it) }

                    // Go to Preview Fragment
                    initPreviewFragment()
                }
            }
    }

    private fun initPreviewFragment() {
        activity
            ?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.container,
                PreviewFragment.newInstance(), PreviewFragment::class.java.name)
            ?.addToBackStack(null)
            ?.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // Permission is granted, start intent to pick images from gallery
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery()
                } else {
                    // Permission denied
                    Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * @param uri The content URI that needs to be converted
     * @return The converted filepath string of the selected image
     */
    private fun getPathFromUri(uri: Uri): String {
        lateinit var imagePath: String

        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(
            uri,
            proj, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(proj[0])
                imagePath = cursor.getString(columnIndex)
            }
        }
        cursor?.close()

        return imagePath
    }
}
