package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.R
import com.helloyatri.data.model.HelpCenterTabs
import com.helloyatri.data.model.TabType
import com.helloyatri.databinding.AccountHelpCenterFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AccountHelpCenterPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountHelpCenterFragment : BaseFragment<AccountHelpCenterFragmentBinding>() {

    private var helpCenterPagerAdapter: AccountHelpCenterPagerAdapter? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountHelpCenterFragmentBinding {
        return AccountHelpCenterFragmentBinding.inflate(layoutInflater)
    }

    private val listTab by lazy {
        arrayListOf(
                HelpCenterTabs(title = getString(R.string.label_faq), status = TabType.FAQ),
                HelpCenterTabs(title = getString(R.string.label_contact_us),
                        status = TabType.CONTACT_US),
        )
    }

    override fun bindData() {
        setViewPager()
        setUpClickListener()
    }

    private fun setViewPager() = with(binding) {
        helpCenterPagerAdapter =
                AccountHelpCenterPagerAdapter(this@AccountHelpCenterFragment, listTab)

        viewPager.apply {
            adapter = helpCenterPagerAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listTab[position].title
        }.attach()
    }

    private fun setUpClickListener() = with(binding) {
        imageViewBack.setOnClickListener {
            navigator.goBack()
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}