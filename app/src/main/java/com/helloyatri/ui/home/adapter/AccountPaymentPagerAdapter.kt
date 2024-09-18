package com.helloyatri.ui.home.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.helloyatri.data.model.PaymentTab
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.ui.home.fragment.AccountPaymentAcceptedFragment
import com.helloyatri.ui.home.fragment.AccountPaymentReqAcceptFragment

class AccountPaymentPagerAdapter(
    fragmentActivity: Fragment, private var tablist: ArrayList<PaymentTab>
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {

        when (tablist[position].status) {
            TabTypeForPayment.ACCEPTED -> {
                val accountPaymentAcceptedFragment = AccountPaymentAcceptedFragment()
                accountPaymentAcceptedFragment.arguments =
                    bundleOf("status" to tablist[position].status)
                return accountPaymentAcceptedFragment
            }

            else -> {
                val accountPaymentFragment = AccountPaymentReqAcceptFragment()
                accountPaymentFragment.arguments = bundleOf("status" to tablist[position].status)
                return accountPaymentFragment
            }
        }
    }

    override fun getItemCount(): Int {
        return tablist.size
    }
}
