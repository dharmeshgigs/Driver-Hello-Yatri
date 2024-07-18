package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.data.model.DriverDocuments
import com.helloyatri.databinding.AuthDriverDocumentsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class CommonAdapter :
    AdvanceRecycleViewAdapter<CommonAdapter.ViewHolder, DataDocument>() {

    inner class ViewHolder(private val binding: AuthDriverDocumentsRowItemBinding) :
        BaseHolder<DataDocument>(binding.root) {

        override fun bind(item: DataDocument) = with(binding) {
            textViewDocumentName.text = item.name

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

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: DataDocument) {
        holder.bind(item)
    }
}