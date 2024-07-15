package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.data.model.PaymentTab
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.databinding.AccountPaymentFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AccountPaymentPagerAdapter
import com.helloyatri.ui.home.dialog.CalenderDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentFragment : BaseFragment<AccountPaymentFragmentBinding>() {

    private var accountPaymentPagerAdapter: AccountPaymentPagerAdapter? = null

    private val listTab by lazy {
        arrayListOf(
                PaymentTab(title = "Requested", status = TabTypeForPayment.REQUESTED),
                PaymentTab(title = "Accepted", status = TabTypeForPayment.ACCEPTED),
        )
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountPaymentFragmentBinding {
        return AccountPaymentFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setViewPager()
        setUpClickListener()
    }

    private fun setViewPager() = with(binding) {
        accountPaymentPagerAdapter =
                AccountPaymentPagerAdapter(this@AccountPaymentFragment, listTab)

        viewPager.apply {
            adapter = accountPaymentPagerAdapter
        }
        viewPager.isUserInputEnabled = true
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listTab[position].title
        }.attach()
    }

    private fun setUpClickListener() = with(binding) {
        imageViewBack.setOnClickListener {
            navigator.goBack()
        }

        imageViewCalendar.setOnClickListener {
            CalenderDialog{}.show(childFragmentManager,"")
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}