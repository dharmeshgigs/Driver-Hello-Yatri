package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.response.RideActivityTabs
import com.helloyatri.ui.home.fragment.AllRideStatusFragment

class RideActivityPagerAdapter(fragmentActivity: Fragment,
                               private var tablist: ArrayList<RideActivityTabs>) :
        FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val allRideStatusFragment = AllRideStatusFragment()
        allRideStatusFragment.arguments = bundleOf("status" to tablist[position].status)
        return allRideStatusFragment
    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}