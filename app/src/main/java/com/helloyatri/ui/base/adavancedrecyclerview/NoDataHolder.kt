package com.helloyatri.ui.base.adavancedrecyclerview

import com.helloyatri.databinding.AdvancedRecyclerViewNoDataHolderBinding


class NoDataHolder<E>(private val binding: AdvancedRecyclerViewNoDataHolderBinding) :
    BaseHolder<E>(binding.root) {

    fun setErrorText(errorText: String) = with(binding) {
        textViewErrorMessage.text = errorText
    }

    override fun bind(item: E) {
        //
    }
}