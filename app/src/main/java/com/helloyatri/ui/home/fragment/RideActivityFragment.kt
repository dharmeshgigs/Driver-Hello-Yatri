package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.data.model.RideActivityTabs
import com.helloyatri.databinding.RideActivityFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.RideActivityPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideActivityFragment : BaseFragment<RideActivityFragmentBinding>() {

    private var rideActivityPagerAdapter: RideActivityPagerAdapter? = null
    private val apiViewModel by activityViewModels<ApiViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): RideActivityFragmentBinding {
        return RideActivityFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpClickListener()
        initObserver()
        apiViewModel.getAllRideData(mutableMapOf())
    }

    private fun initObserver() {
        apiViewModel.getAllRideLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (resource.status) {
                    com.helloyatri.network.Status.SUCCESS -> {
                        hideLoader()
                        setViewPager()
                    }
                    com.helloyatri.network.Status.ERROR -> {
                        hideLoader()
                        setViewPager()
                    }
                    com.helloyatri.network.Status.LOADING -> {
//                        showLoader()
                    }
                }
            }
        }
    }

    private fun setViewPager() = with(binding) {
        rideActivityPagerAdapter = RideActivityPagerAdapter(this@RideActivityFragment, apiViewModel.rideTabs)
        viewPagerFavorite.apply {
            adapter = rideActivityPagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPagerFavorite) { tab, position ->
            apiViewModel.rideTabs[position].strResTitle?.let {
                tab.text = getString(it)
            } ?: run {
                tab.text = apiViewModel.rideTabs[position].title
            }

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