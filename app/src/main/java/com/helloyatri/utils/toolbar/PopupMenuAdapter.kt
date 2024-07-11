package com.helloyatri.utils.toolbar

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.databinding.CustomToolbarPopupMenuRowBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.setIcon
import com.helloyatri.utils.extension.toBinding

class PopupMenuAdapter : AdvanceRecycleViewAdapter<PopupMenuAdapter.ViewHolder, MenuItem>() {

    inner class ViewHolder(private val binding: CustomToolbarPopupMenuRowBinding) : BaseHolder<MenuItem>(binding.root) {

        override fun bind(item: MenuItem) = with(binding) {
            item.title?.let {
                textViewPopupMenu.text = item.title
            }

            item.icon?.let { icon ->
                textViewPopupMenu.setIcon(startIcon = icon)
            }

            item.iconColor?.let { drawableTint ->
                textViewPopupMenu.supportCompoundDrawablesTintList = ContextCompat.getColorStateList(context, drawableTint)
            }

            item.titleColor?.let { titleColor ->
                textViewPopupMenu.setTextColor(getColor(titleColor))
            }

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }
}