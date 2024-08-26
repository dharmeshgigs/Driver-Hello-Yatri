package com.helloyatri.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.ScheduleRide
import com.helloyatri.databinding.ItemMainScheduleRideBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isVisible
import com.helloyatri.utils.extension.toBinding

class ScheduleRideMainAdapter :
        AdvanceRecycleViewAdapter<ScheduleRideMainAdapter.ViewHolder, ScheduleRide>() {

    inner class ViewHolder(private val binding: ItemMainScheduleRideBinding) :
            BaseHolder<ScheduleRide>(binding.root) {

        private val scheduleRideSubAdapter by lazy {
            ScheduleRideSubAdapter()
        }

        override fun bind(item: ScheduleRide) = with(binding) {
            textViewTitle.text = item.title
            setAdapter(item)
            handleExpandCollapse(item)
            setUpClickListener(item)
        }

        private fun setAdapter(item: ScheduleRide) {
            binding.recyclerViewSubNotification.apply {
                adapter = scheduleRideSubAdapter
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)

                item.subList?.let { scheduleRideSubAdapter.setItems(it, 1) }
//                scheduleRideSubAdapter.setOnItemClickPositionListener{ item1: RidePickUps, position: Int ->
//                    onClickListener?.invoke(item)
//                }
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