package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.helloyatri.databinding.AllRideStatusFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AllRidesStatusAdapter
import com.helloyatri.utils.Constants
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AllRideStatusFragment : BaseFragment<AllRideStatusFragmentBinding>() {
    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var rideStatus: String? = null

    private val allRidesStatusAdapter by lazy {
        AllRidesStatusAdapter(onEmergencyClick = {

        }, onEndHereClick = {

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AllRideStatusFragmentBinding {
        return AllRideStatusFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        rideStatus = arguments?.getString("status")
        initObservers()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerView.adapter = allRidesStatusAdapter
    }

    private fun initObservers() {
        apiViewModel.getActiveRideLiveData.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (resource.status) {
                    com.helloyatri.network.Status.SUCCESS -> {
                        hideProgressBar()
                        allRidesStatusAdapter.setItems(apiViewModel.activeTrips.toList(), 1)
                        showPlaceHolder()
                    }

                    com.helloyatri.network.Status.ERROR -> {
                        hideProgressBar()
                        allRidesStatusAdapter.setItems(emptyList(), 1)
                        showPlaceHolder()
                    }

                    com.helloyatri.network.Status.LOADING -> {
                        showProgressBar()
                    }
                }
            }
        }
    }

    private fun showPlaceHolder() = with(binding) {
        allRidesStatusAdapter.items?.takeIf { it.isNotEmpty() }?.let {
            textViewPlaceholder.gone()
        } ?: run {
            textViewPlaceholder.visible()
        }
        recyclerView.invalidate()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
        binding.textViewPlaceholder.gone()
        rideStatus?.let {
            apiViewModel.getActiveRideData(mutableMapOf<String, String>().apply {
                put(Constants.PARAM_FILTER_PARAMETER, "ACTIVE")
            })
        }
    }

    private fun showProgressBar() = with(binding) {
        progressBar.visible()
    }

    private fun hideProgressBar() = with(binding) {
        progressBar.gone()
    }
}