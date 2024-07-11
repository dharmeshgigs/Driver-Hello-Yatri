package com.helloyatri.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.response.RidePickUps
import com.helloyatri.data.response.ScheduleRide
import com.helloyatri.databinding.ItemMainScheduleRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.toBinding

class IntercityRideMainAdapter :
        AdvanceRecycleViewAdapter<IntercityRideMainAdapter.ViewHolder, ScheduleRide>() {

    private var onClickAcceptOrDecline: ((item: ScheduleRide, subItem: RidePickUps, action: String) -> Unit)? =
            null

    fun setOnAcceptOrDeclineClickListener(
            onClickAcceptOrDecline: (item: ScheduleRide, subItem: RidePickUps, action: String) -> Unit) {
        this.onClickAcceptOrDecline = onClickAcceptOrDecline
    }


    inner class ViewHolder(private val binding: ItemMainScheduleRideBinding) :
            BaseHolder<ScheduleRide>(binding.root) {

        private val intercityRideSubAdapter by lazy {
            IntercityRideSubAdapter()
        }

        override fun bind(item: ScheduleRide) = with(binding) {
            textViewTitle.text = item.title
            setAdapter(item)
            handleExpandCollapse(item)
            setUpClickListener(item)
        }

        private fun setAdapter(item: ScheduleRide) {
            binding.recyclerViewSubNotification.apply {
                adapter = intercityRideSubAdapter
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
                item.subList?.let { intercityRideSubAdapter.setItems(it, 1) }
            }
        }

        fun handleExpandCollapse(item: ScheduleRide) = with(binding) {
            imageViewIcon.setImageResource(
                    if (item.isExpanded) R.drawable.ic_drop_down else R.drawable.ic_up)
            recyclerViewSubNotification.isVisible(item.isExpanded)
        }

        private fun setUpClickListener(item: ScheduleRide) = with(binding) {
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

            intercityRideSubAdapter.setOnDeclineOrAcceptClickListener { subItem, action ->
                onClickAcceptOrDecline?.invoke(item, subItem, action)
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun toggleIsExpandedValue(item: ScheduleRide) {
            item.isExpanded = !item.isExpanded
            notifyDataSetChanged()
        }

    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: ScheduleRide,
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