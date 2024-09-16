package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.model.NotificationsData
import com.helloyatri.databinding.ItemSubNotificationBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.toBinding

class NotificationSubAdapter(override var onClickListener: ((NotificationsData) -> Unit)?) :
    AdvanceRecycleViewAdapter<NotificationSubAdapter.ViewHolder, NotificationsData>() {

    inner class ViewHolder(private val binding: ItemSubNotificationBinding) :
        BaseHolder<NotificationsData>(binding.root) {

        override fun bind(item: NotificationsData): Unit = with(binding) {
            textViewMessage.text = item.title.nullify()
            textViewDescription.text = item.message.nullify()
            textViewTime.text = item.createdAt.nullify()
            if (item.isRead == true){
                textViewMessage.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
                textViewDescription.setTextColor(ContextCompat.getColorStateList(context, R.color.hintColor))
            }else{
                textViewMessage.setTextColor(ContextCompat.getColorStateList(context, R.color.colorAccent))
                textViewDescription.setTextColor(ContextCompat.getColorStateList(context, R.color.black))
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