package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.ui.home.fragment.AllRideStatusFragment
import com.helloyatri.ui.home.fragment.CancelledRideFragment
import com.helloyatri.ui.home.fragment.CompletedRideFragment

class RideActivityPagerAdapter(fragmentActivity: Fragment,
                               private var tablist: ArrayList<RideActivityTabs>) :
        FragmentStateAdapter(fragmentActivity) {


    override fun createFragment(position: Int): Fragment {
        when(tablist[position].paramType) {
            "ACTIVE" ->  {
                val allRideStatusFragment = AllRideStatusFragment()
                allRideStatusFragment.arguments = bundleOf("status" to tablist[position].paramType)
                return allRideStatusFragment
            }
            "COMPLETED" ->  {
                val allRideStatusFragment = CompletedRideFragment()
                allRideStatusFragment.arguments = bundleOf("status" to tablist[position].paramType)
                return allRideStatusFragment
            }
            else -> {
                val allRideStatusFragment = CancelledRideFragment()
                allRideStatusFragment.arguments = bundleOf("status" to tablist[position].paramType)
                return allRideStatusFragment
            }
        }

    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}