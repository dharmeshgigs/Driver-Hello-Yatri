package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.databinding.AccountPaymentReqAcceptFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPayment
import com.helloyatri.utils.Constants
import com.helloyatri.utils.DateUtils
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AccountPaymentAcceptedFragment : BaseFragment<AccountPaymentReqAcceptFragmentBinding>() {

    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var status: String? = null

    private val adapterAccountPayment by lazy {
        AdapterAccountPayment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getString("status")
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): AccountPaymentReqAcceptFragmentBinding {
        return AccountPaymentReqAcceptFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initObservers()
        setUpRecyclerView()
    }

    private fun initObservers() {
        apiViewModel.getAcceptedPaymentTripsLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.progressBar.gone()
                        setUpData()
                        showPlaceHolder()
                    }

                    Status.ERROR -> {
                        binding.progressBar.gone()
                        setUpData()
                        showPlaceHolder()
                    }

                    Status.LOADING -> {
                        binding.progressBar.show()
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewPayment.apply {
            adapter = adapterAccountPayment
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpData() {
        adapterAccountPayment.setItems(apiViewModel.requestedPaymentTrips.value?.data ?: emptyList(), 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onResume() {
        super.onResume()
        binding.textViewPlaceholder.gone()
        binding.progressBar.show()
        status?.let {
            if (status === "ACCEPTED") {
                if(apiViewModel.filterDate.isNotEmpty()) {
                    invokeApi(apiViewModel.filterDate)
                } else {
                    val convTime = DateUtils.format(
                        Date(),
                        DateUtils.DateFormat.YYYY_MM_DD,
                        DateUtils.DateTimeZone.DEFAULT
                    )
                    apiViewModel.filterDate = convTime
                    invokeApi(convTime)
                }
            }
        }
    }

    private fun showPlaceHolder() = with(binding) {
        adapterAccountPayment.items?.takeIf { it.isNotEmpty() }?.let {
            textViewPlaceholder.gone()
        } ?: run {
            textViewPlaceholder.visible()
        }
    }

    fun invokeApi(date: String) {
        apiViewModel.getAcceptedTripPayments(mutableMapOf<String, String>().apply {
            put(Constants.PARAM_FILTER_PARAMETER, "ACCEPTED")
            put(Constants.PARAM_FILTER_DATE, date)
        })
    }
}