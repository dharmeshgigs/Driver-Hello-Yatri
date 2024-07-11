package com.helloyatri.ui.manager

import android.util.Pair
import android.view.View
import androidx.annotation.UiThread
import com.helloyatri.ui.base.BaseFragment


@UiThread
interface FragmentHandler {

    fun openFragment(baseFragment: BaseFragment<*>, option: Option, isToBackStack: Boolean, tag: String, sharedElements: List<Pair<View, String>>?)

    fun showFragment(fragmentToShow: BaseFragment<*>, vararg fragmentToHide: BaseFragment<*>)

    fun clearFragmentHistory(tag: String?)


    enum class Option {
        ADD, REPLACE, SHOW, HIDE
    }
}