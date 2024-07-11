package com.helloyatri.ui.base.adavancedrecyclerview

import androidx.annotation.UiThread

interface OnSelectionChangeListener<T> {

    @UiThread
    fun onSelectionChange(t: T, isSelected: Boolean)
}
