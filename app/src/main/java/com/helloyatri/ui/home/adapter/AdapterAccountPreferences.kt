package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.Preference
import com.helloyatri.databinding.AccountPreferenceRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.toBinding

class AdapterAccountPreferences(override var onClickListener: ((Preference) -> Unit)?) :
        AdvanceRecycleViewAdapter<AdapterAccountPreferences.ViewHolder, Preference>() {

    inner class ViewHolder(private val binding: AccountPreferenceRowItemBinding) :
            BaseHolder<Preference>(binding.root) {

        override fun bind(item: Preference) = with(binding) {
            textViewPreferredNavigation.text = item.title
            switchOnOff.isChecked = item.value.equals("Yes")
            switchOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked){
                    item.value = "No"
                } else {
                    item.value = "Yes"
                }
                onClickListener?.invoke(item)
            }
            root.setOnClickListener {
                if(item.value.equals("No")){
                    item.value = "Yes"
                } else {
                    item.value = "No"
                }
                notifyItemChanged(bindingAdapterPosition)
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: Preference) {
        holder.bind(item)
    }
}
