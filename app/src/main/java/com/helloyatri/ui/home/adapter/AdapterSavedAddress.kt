package com.helloyatri.ui.home.adapter

import android.view.View
import android.view.ViewGroup
import com.helloyatri.R
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
            if(item.id == 0) {
                textViewSave.text = context.getString(R.string.label_add_new)
                textViewAddress.visibility = View.GONE
                imageViewSavedAddress.setImageResource(R.drawable.ic_add_new_address)
            } else {
                textViewSave.text = item.name ?:""
                textViewAddress.visibility = View.VISIBLE
                textViewAddress.text = item.location ?:""
                imageViewSavedAddress.setImageResource(R.drawable.image_saved_address)
            }
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
