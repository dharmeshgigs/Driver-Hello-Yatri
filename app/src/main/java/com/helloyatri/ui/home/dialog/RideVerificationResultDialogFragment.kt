package com.helloyatri.ui.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.helloyatri.R
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.RideVerificationResultDialogFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.utils.AppUtils
import com.helloyatri.utils.extension.setDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideVerificationResultDialogFragment :
    BaseDialogFragment<RideVerificationResultDialogFragmentBinding>() {

    private lateinit var successResult: String
    private var tripRiderModel: TripRiderModel? = null
    private val apiViewModel: ApiViewModel by activityViewModels()
    private var firstName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
        firstName = AppUtils.getFirstName(tripRiderModel?.riderDetails?.name ?: "")
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): RideVerificationResultDialogFragmentBinding {
        return RideVerificationResultDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setUpViews()
        setClickListener()
    }

    private fun setUpViews() {
        successResult = arguments?.getString(STATUS).toString()
        apiViewModel?.popUp?.let {
            when (it.status) {
                "success" -> {
                    successTrip()
                }

                "error" -> {
                    failedTrip()
                }
            }
        } ?: run {
            if (successResult == SUCCESS) {
                successTrip()
            } else {
                failedTrip()
            }
        }

    }

    private fun successTrip() = with(binding) {
        textViewTitle.text =
            apiViewModel.popUp?.POPUPTITLE ?: getString(R.string.label_your_trip_is_verified)
        textViewDescription.text = apiViewModel.popUp?.POPUPMESSAGE ?: String.format(
            getString(R.string.label_dynamic_your_code_is_match_with_pooja_start_your_ride_now),
            firstName
        )
        imageViewResult.setDrawable(R.drawable.ic_success_trip_verified)
        textViewLetsRide.text =
            apiViewModel.popUp?.POPUPBUTTONLBL ?: getString(R.string.label_okay_let_s_ride)
    }

    private fun failedTrip() = with(binding) {
        textViewTitle.text =
            apiViewModel.popUp?.POPUPTITLE ?: getString(R.string.label_verification_failed)
        textViewDescription.text = apiViewModel.popUp?.POPUPMESSAGE ?: String.format(
            getString(R.string.label_dynamic_your_code_is_does_not_match_with_pooja_do_you_want_to_regenerate_the_code),
            firstName
        )
        imageViewResult.setDrawable(R.drawable.ic_failed_trip_verification)
        textViewLetsRide.text =
            apiViewModel.popUp?.POPUPBUTTONLBL ?: getString(R.string.label_regenerate_code)
    }

    private fun setClickListener() = with(binding) {
        textViewLetsRide.setOnClickListener {
            if (successResult == SUCCESS) {
                apiViewModel._tripStartLiveData.postValue(
                    true
                )
                dismissAllowingStateLoss()
            } else {
                val rideVerificationDialogFragment = RideVerificationDialogFragment {
                    if (it == SUCCESS) {
                        successResult = SUCCESS
                        successTrip()
                    } else {
                        failedTrip()
                    }
                }
                rideVerificationDialogFragment.arguments = bundleOf("isForRegenerateCode" to true)
                rideVerificationDialogFragment.show(
                    childFragmentManager,
                    RideVerificationResultDialogFragment::class.java.simpleName
                )
            }
        }
    }

    companion object {
        const val STATUS = "STATUS"
        const val SUCCESS = "SUCCESS"
        const val FAILED = "FAILED"
        const val RIDE_VERIFIED = "RIDE_VERIFIED"
    }
}