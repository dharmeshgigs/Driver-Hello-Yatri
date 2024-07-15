package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.helloyatri.data.model.CancelRideReasons
import com.helloyatri.databinding.ItemRowCancelReasonsBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.toBinding

class AdapterCancelRideReasons :
        AdvanceRecycleViewAdapter<AdapterCancelRideReasons.ViewHolder, CancelRideReasons>() {

    inner class ViewHolder(private val binding: ItemRowCancelReasonsBinding) :
            BaseHolder<CancelRideReasons>(binding.root) {

        override fun bind(item: CancelRideReasons): Unit = with(binding) {
            textViewTitle.text = item.title
            imageViewCheckBox.isSelected = item.isSelected
            editTextDescription.setLines(4)

            if (item.isSelected && item.title == "Other") {
                editTextDescription.isVisible(true)
            } else {
                editTextDescription.isVisible(false)
            }

            editTextDescription.addTextChangedListener {
                if (!it.isNullOrEmpty()) {
                    item.reportReasonOther = it.toString()
                }
            }

            root.setOnClickListener {
                selectSingleItem(adapterPosition) { prevSelectedItem ->
                    prevSelectedItem.isSelected = false
                }
                item.isSelected = true
                notifyItemChanged(adapterPosition)
                onClickListener?.invoke(item)
            }
        }

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lastItemSelectedPos = getItemIndex(items!!.find { it.isSelected })
        return ViewHolder(parent.toBinding())
    }
}