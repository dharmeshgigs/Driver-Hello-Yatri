package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.helloyatri.R
import com.helloyatri.data.model.AccountPaymentData
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.AccountPaymentReqAcceptFragmentBinding
import com.helloyatri.databinding.TripReportCrashFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountPayment
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.gone
import com.helloyatri.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripReportCrashFragment : BaseFragment<TripReportCrashFragmentBinding>() {

    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var tripRiderModel: TripRiderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
    }
    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): TripReportCrashFragmentBinding {
        return TripReportCrashFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initViews()
        initObservers()
        setUpRecyclerView()
        setClickListeners()
    }

    private fun initViews() = with(binding){
        textViewLocationAddress.text = "ABC"
        includedFullName.textViewTitle.text = getString(R.string.label_description)
        includedFullName.editText.hint = getString(R.string.label_description)
        buttonSubmit.isClickable = false
        buttonSubmit.isEnabled = false
        buttonSubmit.enableTextView(false)
    }

    private fun setClickListeners() = with(binding){
        buttonSubmit.setOnClickListener {

        }
    }

    private fun initObservers() {
        apiViewModel.tripReportCrashLiveData.observe(this){resource->
            resource?.let {
                when(resource.status){
                    Status.SUCCESS -> {
                        navigator.goBack()
                    }
                    Status.ERROR -> {
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun setUpRecyclerView() = with(binding) {

    }

    private fun setUpData() {
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}