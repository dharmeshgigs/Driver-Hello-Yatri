package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.SavedAddress
import com.helloyatri.databinding.AccountSavedAddressRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class AdapterSavedAddress :
        AdvanceRecycleViewAdapter<AdapterSavedAddress.ViewHolder, SavedAddress>() {

    inner class ViewHolder(private val binding: AccountSavedAddressRowItemBinding) :
            BaseHolder<SavedAddress>(binding.root) {

        override fun bind(item: SavedAddress) = with(binding) {
            textViewSave.text = String.format("Save %s", adapterPosition + 1)
            textViewAddress.text = item.saveAddress

            root.setOnClickListener {
                onClickListener?.invoke(item)
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
