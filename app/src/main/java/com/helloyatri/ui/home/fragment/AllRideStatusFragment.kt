package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.data.response.Status
import com.helloyatri.databinding.AllRideStatusFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AllRidesStatusAdapter
import com.helloyatri.utils.getActiveRideList
import com.helloyatri.utils.getCancelledRideList
import com.helloyatri.utils.getCompletedRideList
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AllRideStatusFragment : BaseFragment<AllRideStatusFragmentBinding>() {

    private var rideStatus: Status? = null

    private val allRidesStatusAdapter by lazy {
        AllRidesStatusAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AllRideStatusFragmentBinding {
        return AllRideStatusFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        rideStatus = arguments?.getParcelable("status")
        setAdapter()
    }

    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = allRidesStatusAdapter
            when (rideStatus) {
                Status.ACTIVE -> {
                    allRidesStatusAdapter.setItems(requireActivity().getActiveRideList(), 1)
                }

                Status.COMPLETED -> {
                    allRidesStatusAdapter.setItems(requireActivity().getCompletedRideList(), 1)
                }

                else -> {
                    allRidesStatusAdapter.setItems(requireActivity().getCancelledRideList(), 1)
                }
            }
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}