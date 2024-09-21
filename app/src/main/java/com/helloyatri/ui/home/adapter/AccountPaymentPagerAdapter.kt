package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.model.PaymentTab
import com.helloyatri.ui.home.fragment.AccountPaymentAcceptedFragment
import com.helloyatri.ui.home.fragment.AccountPaymentRequestedFragment

class AccountPaymentPagerAdapter(
    fragmentActivity: Fragment, private var tablist: ArrayList<PaymentTab>
) : FragmentStateAdapter(fragmentActivity) {

    val fragmentList = arrayListOf<Fragment>()

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val accountPaymentFragment = AccountPaymentRequestedFragment()
                accountPaymentFragment.arguments = bundleOf("status" to tablist[position].status)
                fragmentList.add(accountPaymentFragment)
                return accountPaymentFragment
            }

            else -> {
                val accountPaymentAcceptedFragment = AccountPaymentAcceptedFragment()
                accountPaymentAcceptedFragment.arguments =
                    bundleOf("status" to tablist[position].status)
                fragmentList.add(accountPaymentAcceptedFragment)
                return accountPaymentAcceptedFragment
            }
        }
    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}
