package com.helloyatri.ui.auth.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.model.DataDocument
import com.helloyatri.databinding.AuthDriverDocumentsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.toBinding

class CommonAdapter :
    AdvanceRecycleViewAdapter<CommonAdapter.ViewHolder, DataDocument>() {

    var type: String = ""

    inner class ViewHolder(private val binding: AuthDriverDocumentsRowItemBinding) :
        BaseHolder<DataDocument>(binding.root) {

        override fun bind(item: DataDocument) = with(binding) {
            textViewDocumentName.text = item.name
            when (type) {
                Constants.VEHICLE_DOCUMENT, Constants.VEHICLE_PHOTO -> {
                    when (item.uploaded_vehicle_document?.status) {
                        1, 0 -> {
                            root.strokeLineColor =
                                ContextCompat.getColor(root.context, R.color.strokeGreenColor)
                        }

                        2 -> {
                            root.strokeLineColor =
                                ContextCompat.getColor(root.context, R.color.redColor)
                        }

                        else -> {
                            root.strokeLineColor = ContextCompat.getColor(root.context, R.color.grey)
                        }
                    }
                }

                else -> {
                    when (item.uploadedDriverDocument?.status) {
                        1, 0 -> {
                            root.strokeLineColor =
                                ContextCompat.getColor(root.context, R.color.strokeGreenColor)
                        }

                        2 -> {
                            root.strokeLineColor =
                                ContextCompat.getColor(root.context, R.color.redColor)
                        }

                        else -> {
                            root.strokeLineColor = ContextCompat.getColor(root.context, R.color.grey)
                        }
                    }
                }
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