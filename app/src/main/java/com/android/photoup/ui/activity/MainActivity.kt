package com.android.photoup.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.photoup.R
import com.android.photoup.ui.fragment.SelectFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(SelectFragment.newInstance(), SelectFragment::class.java.simpleName)
        }
    }

    /**
     * @param fragment The fragment to be inflated
     * @param fragmentName The fragment's tag
     */
    private fun replaceFragment(fragment: Fragment, fragmentName: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, fragmentName)
            .commit()
    }
}
