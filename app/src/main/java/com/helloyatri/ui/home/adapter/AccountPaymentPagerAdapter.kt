package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.response.PaymentTab
import com.helloyatri.ui.home.fragment.AccountPaymentReqAcceptFragment

class AccountPaymentPagerAdapter(fragmentActivity: Fragment,
                                 private var tablist: ArrayList<PaymentTab>) :
        FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        val accountPaymentFragment = AccountPaymentReqAcceptFragment()
        accountPaymentFragment.arguments = bundleOf("tabType" to tablist[position].status)
        return accountPaymentFragment
    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}
