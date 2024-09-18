package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.data.model.AccountPaymentData
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.databinding.AccountPaymentReqAcceptFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPayment
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentAcceptedFragment : BaseFragment<AccountPaymentReqAcceptFragmentBinding>() {

    private val apiViewModel by viewModels<ApiViewModel>()

    private val adapterAccountPayment by lazy {
        AdapterAccountPayment()
    }

    private val accountPaymentDataList = ArrayList<AccountPaymentData>()
    private var status : TabTypeForPayment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = arguments?.getParcelable("tabType")
    }
    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountPaymentReqAcceptFragmentBinding {
        return AccountPaymentReqAcceptFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initObservers()
        setUpRecyclerView()

    }

    private fun initObservers() {
        if(status === TabTypeForPayment.REQUESTED) {
//            apiViewModel.getAllPaymentLiveData.observe(this){resource->
//                when(resource.status){
//                    Status.SUCCESS -> {
//                        binding.progressBar.gone()
//                        setUpData()
//                    }
//                    Status.ERROR -> {
//                        binding.progressBar.gone()
//                        setUpData()
//                    }
//                    Status.LOADING -> {
//                        binding.progressBar.show()
//                    }
//                }
//            }
        } else if(status === TabTypeForPayment.ACCEPTED) {
//            apiViewModel.getAcceptedPaymentLiveData.observe(this){resource->
//                when(resource.status){
//                    Status.SUCCESS -> {
//                        binding.progressBar.gone()
//                        setUpData()
//                    }
//                    Status.ERROR -> {
//                        binding.progressBar.gone()
//                        setUpData()
//                    }
//                    Status.LOADING -> {
//                        binding.progressBar.show()
//                    }
//                }
//            }
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

    override fun onResume() {
        super.onResume()
        binding.textViewPlaceholder.gone()
        binding.progressBar.show()
        status?.let {
            if(status === TabTypeForPayment.REQUESTED) {
//                apiViewModel.getAllPaymentAPI()
            } else if(status === TabTypeForPayment.ACCEPTED) {
//                apiViewModel.getAcceptedPaymentAPI()
            }
        }
    }
}