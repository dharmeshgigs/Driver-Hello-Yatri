package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.AccountPreferences
import com.helloyatri.databinding.AccountPreferenceRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class AdapterAccountPreferences :
        AdvanceRecycleViewAdapter<AdapterAccountPreferences.ViewHolder, AccountPreferences>() {

    inner class ViewHolder(private val binding: AccountPreferenceRowItemBinding) :
            BaseHolder<AccountPreferences>(binding.root) {

        override fun bind(item: AccountPreferences) = with(binding) {
            textViewPreferredNavigation.text = item.accountPreferences
            switchOnOff.isChecked = item.isSwitchOn == true

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: AccountPreferences) {
        holder.bind(item)
    }
}
