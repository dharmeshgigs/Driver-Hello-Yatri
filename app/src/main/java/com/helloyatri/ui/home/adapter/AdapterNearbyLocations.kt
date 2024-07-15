package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.data.model.NearByLocation
import com.helloyatri.databinding.RowItemNearByLocationBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.isInVisible
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding

class AdapterNearbyLocations :
        AdvanceRecycleViewAdapter<AdapterNearbyLocations.ViewHolder, NearByLocation>() {

    inner class ViewHolder(private val binding: RowItemNearByLocationBinding) :
            BaseHolder<NearByLocation>(binding.root) {

        override fun bind(item: NearByLocation) = with(binding) {
            if (adapterPosition == items!!.size - 1) {
                viewDivider.isInVisible(true)
            } else {
                viewDivider.show()
            }

            textViewPlaceName.text = item.placeName
            textViewSubLocation.text = item.subPlaceName
            textViewAcceptanceRatio.text =
                    context.getString(R.string._n_nacceptance_nratio, item.acceptanceRatio)

            root.setOnClickListener {
                onClickViewListener?.invoke(item, it)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: NearByLocation) {
        holder.bind(item)
    }
}
