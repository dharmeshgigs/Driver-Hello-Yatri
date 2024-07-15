package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.model.HelpCenterTabs
import com.helloyatri.ui.home.fragment.AccountHelpCenterWebViewFragment
import com.helloyatri.ui.home.fragment.AccountHelpCenterWebViewFragment.Companion.TAB_TYPE

class AccountHelpCenterPagerAdapter(fragmentActivity: Fragment,
                                    private var tablist: ArrayList<HelpCenterTabs>) :
        FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val helpCenterWebViewFragment = AccountHelpCenterWebViewFragment()
        helpCenterWebViewFragment.arguments = bundleOf(TAB_TYPE to tablist[position].status)
        return helpCenterWebViewFragment
    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}