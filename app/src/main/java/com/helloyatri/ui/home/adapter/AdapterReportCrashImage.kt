package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.ReportCrashImageDataModel
import com.helloyatri.databinding.AuthDriverPersonalProfilePictureImageRowAddItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding

class AdapterReportCrashImage :
    AdvanceRecycleViewAdapter<AdapterReportCrashImage.ViewHolder, ReportCrashImageDataModel>() {

    inner class ViewHolder(private val binding: AuthDriverPersonalProfilePictureImageRowAddItemBinding) :
        BaseHolder<ReportCrashImageDataModel>(binding.root) {

        override fun bind(item: ReportCrashImageDataModel) = with(binding) {
            if (bindingAdapterPosition == 0) {
                imageViewPic.setImageResource(R.drawable.add_a_photo_24)
                imageViewDelete.hide()
                imageViewPic.setOnClickListener {
                    onClickPositionListener?.invoke(item, bindingAdapterPosition)
                }
            } else {
                imageViewDelete.show()
                imageViewPic.loadImageFromServerWithPlaceHolder(
                    item.image ?: ""
                )
                imageViewDelete.setOnClickListener {
                    onClickPositionListener?.invoke(item, bindingAdapterPosition)
                }
            }

        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(
        holder: ViewHolder,
        position: Int,
        item: ReportCrashImageDataModel
    ) {
        holder.bind(item)
    }
}
