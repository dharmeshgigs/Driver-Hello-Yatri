package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.data.model.AccountPaymentData
import com.helloyatri.databinding.AccountPaymentReqAcceptFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPayment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentReqAcceptFragment : BaseFragment<AccountPaymentReqAcceptFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()

    private val adapterAccountPayment by lazy {
        AdapterAccountPayment()
    }

    private val accountPaymentDataList = ArrayList<AccountPaymentData>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountPaymentReqAcceptFragmentBinding {
        return AccountPaymentReqAcceptFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        apiViewModel.getAllPaymentAPI()
        setUpRecyclerView()

        initObservers()

    }

    private fun initObservers() {
        apiViewModel.getAllPaymentLiveData.observe(this){resource->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                }
                Status.ERROR -> {
                    hideLoader()
                    setUpData()
                }
                Status.LOADING -> showLoader()
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
        accountPaymentDataList.clear()
        for (items in 1..3) {
            accountPaymentDataList.add(AccountPaymentData(
                    userImage = "https://images.unsplash.com/photo-1665779912168-c6d48ec98dcb?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29uJTIwaW1hZ2V8ZW58MHx8MHx8fDA%3D",
                    userName = "Aesha Mehta", paymentType = "Cash Payment",
                    awayDuration = "Was 5 mins Away",
                    startLocation = "Cinemax, Rectory Cottage, Court Road",
                    endLocation = "101 National Dr. San Bruno, CA 94580", distance = "25.5 Km",
                    duration = "45 min", farePrice = "₹ 480"))
        }
        adapterAccountPayment.setItems(accountPaymentDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}