package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.DriverVerification
import com.helloyatri.databinding.AuthDriverVerificationRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.setDrawable
import com.helloyatri.utils.extension.toBinding

class DriverVerificationAdapter :
        AdvanceRecycleViewAdapter<DriverVerificationAdapter.ViewHolder, DriverVerification>() {

    inner class ViewHolder(private val binding: AuthDriverVerificationRowItemBinding) :
            BaseHolder<DriverVerification>(binding.root) {

        override fun bind(item: DriverVerification) = with(binding) {
            imageView.setDrawable(item.image)
            textView.text = item.text
            buttonCall.text = item.button

            buttonCall.setOnClickListener {
                onClickPositionListener?.invoke(item, adapterPosition)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: DriverVerification) {
        holder.bind(item)
    }
}