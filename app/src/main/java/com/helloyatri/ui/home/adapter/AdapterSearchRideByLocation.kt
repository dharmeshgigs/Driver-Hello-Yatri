package com.helloyatri.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.helloyatri.data.response.NearByLocation
import com.helloyatri.databinding.RowItemSearchRideFromLocationBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.setDrawable
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.toBinding


class AdapterSearchRideByLocation :
        AdvanceRecycleViewAdapter<AdapterSearchRideByLocation.MyViewHolder, NearByLocation>(
                arrayListOf()), Filterable {


    var locationList: ArrayList<NearByLocation> = ArrayList()
    var locationListListFiltered: ArrayList<NearByLocation> = ArrayList()

    inner class MyViewHolder(private val binding: RowItemSearchRideFromLocationBinding) :
            BaseHolder<NearByLocation>(binding.root) {


        override fun bind(item: NearByLocation) = with(binding) {
            if (adapterPosition == locationListListFiltered.size - 1) {
                viewDivider.hide()
            } else {
                viewDivider.show()
            }
            imageViewIcon.setDrawable(item.icon)
            textViewPlaceName.text = item.placeName
            textViewSubLocation.text = item.subPlaceName
            root.setOnClickListener {
                onClickViewListener?.invoke(item, it)
            }
        }

    }

    override fun createDataHolder(
            parent: ViewGroup,
            viewType: Int,
    ): MyViewHolder {
        return MyViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(
            holder: MyViewHolder,
            position: Int,
            item: NearByLocation,
    ) {
        holder.bind(locationListListFiltered[position])
    }

    fun setItem(data: ArrayList<NearByLocation>, page: Int) {
        locationList = data
        locationListListFiltered = data
        setItemsWithPage(data, page)
        this.items = locationListListFiltered
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                locationListListFiltered = if (charString.isEmpty()) locationList else {
                    val filteredList = ArrayList<NearByLocation>()
                    locationList.filter {
                                (it.placeName?.lowercase()
                                        ?.contains(constraint.toString().lowercase()) == true)

                            }.forEach { filteredList.add(it) }
                    filteredList

                }
                return FilterResults().apply { values = locationListListFiltered }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                locationListListFiltered = if (results?.values == null) ArrayList()
                else results.values as ArrayList<NearByLocation>
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        isNoData = locationListListFiltered.isEmpty()
        return when {
            isNoData -> 1
            isLoading -> items!!.size + 1
            else -> locationListListFiltered.size
        }
    }
}
