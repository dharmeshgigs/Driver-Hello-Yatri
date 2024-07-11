package com.helloyatri.ui.home.sidemenu

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.LayoutSideMenuRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class SideMenuAdapter : AdvanceRecycleViewAdapter<SideMenuAdapter.ViewHolder, SideMenu>() {

    inner class ViewHolder(private val binding: LayoutSideMenuRowItemBinding) :
            BaseHolder<SideMenu>(binding.root) {

        override fun bind(item: SideMenu) = with(binding) {
            textViewNavigation.text = item.sideMenuName

            if (adapterPosition == items!!.size - 1) {
                textViewNavigation.setTextColor(
                        ContextCompat.getColor(textViewNavigation.context, R.color.seekbarRedColor))
            }

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: SideMenu) {
        holder.bind(item)
    }
}