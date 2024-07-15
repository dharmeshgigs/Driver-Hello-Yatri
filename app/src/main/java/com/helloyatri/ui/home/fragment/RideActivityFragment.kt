package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.R
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.data.model.Status
import com.helloyatri.databinding.RideActivityFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.RideActivityPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideActivityFragment : BaseFragment<RideActivityFragmentBinding>() {

    private var rideActivityPagerAdapter: RideActivityPagerAdapter? = null

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): RideActivityFragmentBinding {
        return RideActivityFragmentBinding.inflate(layoutInflater)
    }

    private val listTab by lazy {
        arrayListOf(
                RideActivityTabs(title = getString(R.string.label_active), status = Status.ACTIVE),
                RideActivityTabs(title = getString(R.string.label_completed),
                        status = Status.COMPLETED),
                RideActivityTabs(title = getString(R.string.label_cancelled),
                        status = Status.CANCELLED),
        )
    }

    override fun bindData() {
        setViewPager()
        setUpClickListener()
    }

    private fun setViewPager() = with(binding) {
        rideActivityPagerAdapter = RideActivityPagerAdapter(this@RideActivityFragment, listTab)
        viewPagerFavorite.apply {
            adapter = rideActivityPagerAdapter
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPagerFavorite) { tab, position ->
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