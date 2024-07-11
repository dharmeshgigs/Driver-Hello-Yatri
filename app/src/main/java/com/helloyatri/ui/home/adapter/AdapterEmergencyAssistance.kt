package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.response.EmergencyAssistance
import com.helloyatri.databinding.ItemRowEmegencyAssistanceBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.setDrawable
import com.helloyatri.utils.extension.toBinding

class AdapterEmergencyAssistance :
        AdvanceRecycleViewAdapter<AdapterEmergencyAssistance.ViewHolder, EmergencyAssistance>() {

    inner class ViewHolder(private val binding: ItemRowEmegencyAssistanceBinding) :
            BaseHolder<EmergencyAssistance>(binding.root) {

        override fun bind(item: EmergencyAssistance): Unit = with(binding) {
            textViewTitle.text = item.title
            imageViewIcon.setDrawable(item.icon)
            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }
}