package com.helloyatri.utils.toolbar

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.CustomToolbarMenuItemRowBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.toBinding
import com.helloyatri.utils.hideView
import com.helloyatri.utils.showView


class MenuItemAdapter : AdvanceRecycleViewAdapter<MenuItemAdapter.ViewHolder, MenuItem>() {

    inner class ViewHolder(private val binding: CustomToolbarMenuItemRowBinding) : BaseHolder<MenuItem>(binding.root) {

        override fun bind(item: MenuItem) = with(binding) {
            textViewBadge.isInVisible(!item.showBadge)
            textViewBadge.text = if (item.badgeCount > 99) getString(R.string.label_99_plus) else item.badgeCount.toString()

            item.iconColor?.let { drawableTint ->
                imageViewMenuIcon.supportImageTintList = ContextCompat.getColorStateList(context, drawableTint)
            }

            item.titleColor?.let { titleColor ->
                textViewTitle.setTextColor(getColor(titleColor))
            }

            when (item.menuItemType) {
                MenuItemType.ICON -> {
                    showView(imageViewMenuIcon)
                    hideView(textViewTitle)
                    item.icon?.let { icon ->
                        imageViewMenuIcon.setImageResource(icon)
                    }
                }

                MenuItemType.TITLE -> {
                    showView(textViewTitle)
                    hideView(imageViewMenuIcon, textViewBadge)
                    item.title?.let {
                        textViewTitle.text = item.title
                    }
                }

                MenuItemType.BOTH -> {
                    showView(imageViewMenuIcon, textViewTitle)
                    item.icon?.let { icon ->
                        imageViewMenuIcon.setImageResource(icon)
                    }
                    item.title?.let {
                        textViewTitle.text = item.title
                    }
                }
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