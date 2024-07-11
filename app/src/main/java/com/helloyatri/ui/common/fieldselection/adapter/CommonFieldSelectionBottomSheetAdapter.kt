package com.helloyatri.ui.common.fieldselection.adapter

import android.view.ViewGroup
import com.helloyatri.databinding.CommonFieldSelectionRowBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.ui.common.fieldselection.data.CommonFieldSelection
import com.helloyatri.utils.extension.toBinding

class CommonFieldSelectionBottomSheetAdapter :
        AdvanceRecycleViewAdapter<CommonFieldSelectionBottomSheetAdapter.ViewHolder, CommonFieldSelection>() {

    inner class ViewHolder(private val binding: CommonFieldSelectionRowBinding) :
            BaseHolder<CommonFieldSelection>(binding.root) {

        override fun bind(item: CommonFieldSelection) = with(binding) {
            textViewOptions.text = item.options
            imageViewCheckBox.isSelected = item.isOptionSelected
            root.setOnClickListener {
                selectSingleItem(adapterPosition) { prevSelectedItem ->
                    prevSelectedItem.isOptionSelected = false
                }
                item.isOptionSelected = true
                notifyItemChanged(adapterPosition)
                onClickPositionListener?.invoke(item, adapterPosition)
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        lastItemSelectedPos = getItemIndex(items!!.find { it.isOptionSelected })
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: CommonFieldSelection) {
        holder.bind(item)
    }
}