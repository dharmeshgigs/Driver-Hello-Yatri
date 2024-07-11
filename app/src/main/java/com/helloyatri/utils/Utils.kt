package com.helloyatri.utils

import android.view.View
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.isVisible

fun showView(vararg viewToShow: View) {
    viewToShow.forEach {
        it.isVisible(true)
    }
}

fun hideView(vararg viewToHide: View) {
    viewToHide.forEach {
        it.isVisible(false)
    }
}

fun invisibleView(vararg viewToMakeInvisible: View) {
    viewToMakeInvisible.forEach {
        it.isInVisible(true)
    }
}
