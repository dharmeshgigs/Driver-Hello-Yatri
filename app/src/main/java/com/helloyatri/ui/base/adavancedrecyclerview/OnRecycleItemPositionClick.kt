package com.helloyatri.ui.base.adavancedrecyclerview

import android.view.View

interface OnRecycleItemPositionClick<T> {
    fun onClick(t: T?, view: View,position:Int)
}
