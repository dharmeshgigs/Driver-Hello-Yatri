package com.helloyatri.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.NotificationsData
import com.helloyatri.databinding.ItemMainNotificationBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.toBinding

class NotificationMainAdapter :
        AdvanceRecycleViewAdapter<NotificationMainAdapter.ViewHolder, NotificationsData>() {

    inner class ViewHolder(private val binding: ItemMainNotificationBinding) :
            BaseHolder<NotificationsData>(binding.root) {

        private val notificationSubAdapter by lazy {
            NotificationSubAdapter()
        }

        override fun bind(item: NotificationsData) = with(binding) {
            textViewTitle.text = item.title
            setAdapter(item)
            handleExpandCollapse(item)
            setUpClickListener(item)
        }

        private fun setAdapter(item: NotificationsData) {
            binding.recyclerViewSubNotification.apply {
                adapter = notificationSubAdapter
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
                item.subList?.let { notificationSubAdapter.setItems(it, 1) }
            }
        }

        fun handleExpandCollapse(item: NotificationsData) = with(binding) {
            imageViewIcon.setImageResource(
                    if (item.isExpanded) R.drawable.ic_drop_down else R.drawable.ic_up)
            recyclerViewSubNotification.isVisible(item.isExpanded)
        }

        private fun setUpClickListener(item: NotificationsData) = with(binding) {
            rootConstraint.setOnClickListener {
                if (item.isExpanded) {
                    items!!.find {
                        getItemIndex(it) > getItemIndex(item) && it.isExpanded == item.isExpanded
                    }?.let {
                        //
                    }
                } else {
                    items!!.find { it.isExpanded == !item.isExpanded }?.let {
                        //
                    }
                }
                toggleIsExpandedValue(item)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun toggleIsExpandedValue(item: NotificationsData) {
            item.isExpanded = !item.isExpanded
            notifyDataSetChanged()
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: NotificationsData,
                                  payloads: MutableList<Any>) {
        super.onBindDataHolder(holder, position, item, payloads)
        if (payloads[0] as? String == EXPAND_COLLAPSE) {
            holder.handleExpandCollapse(item)
        } else {
            holder.bind(item)
        }
    }

    companion object {
        const val EXPAND_COLLAPSE = "expandCollapse"
    }
}