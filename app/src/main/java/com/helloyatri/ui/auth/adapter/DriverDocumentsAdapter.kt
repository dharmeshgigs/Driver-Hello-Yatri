package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.response.DriverDocuments
import com.helloyatri.databinding.AuthDriverDocumentsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class DriverDocumentsAdapter :
        AdvanceRecycleViewAdapter<DriverDocumentsAdapter.ViewHolder, DriverDocuments>() {

    inner class ViewHolder(private val binding: AuthDriverDocumentsRowItemBinding) :
            BaseHolder<DriverDocuments>(binding.root) {

        override fun bind(item: DriverDocuments) = with(binding) {
            textViewDocumentName.text = item.documentName

            if (item.isDataAdded) {
                root.strokeLineColor =
                        ContextCompat.getColor(root.context, R.color.strokeGreenColor)
            } else {
                root.strokeLineColor = ContextCompat.getColor(root.context, R.color.grey)
            }

            root.setOnClickListener {
                onClickPositionListener?.invoke(item, adapterPosition)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: DriverDocuments) {
        holder.bind(item)
    }
}