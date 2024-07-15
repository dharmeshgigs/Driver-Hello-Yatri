package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.FragmentStartRideSavedLocationRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.toBinding

class StartRideSavedAddressAdapter :
        AdvanceRecycleViewAdapter<StartRideSavedAddressAdapter.ViewHolder, SavedAddress>() {

    inner class ViewHolder(private val binding: FragmentStartRideSavedLocationRowItemBinding) :
            BaseHolder<SavedAddress>(binding.root) {

        override fun bind(item: SavedAddress) = with(binding) {
            if (!item.isAddressAdded) {
                imageViewAddressAdded.isVisible(false)
                imageViewAddress.isVisible(true)
                textViewAddress.text = getString(R.string.label_add_new)
            } else {
                imageViewAddressAdded.isVisible(true)
                imageViewAddress.isInVisible(true)
                textViewAddress.text = String.format("Save %s", adapterPosition)
            }

            root.setOnClickListener {
                if (adapterPosition == 0) {
                    onClickListener?.invoke(item)
                }
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: SavedAddress) {
        holder.bind(item)
    }
}
