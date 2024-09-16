package com.helloyatri.ui.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.helloyatri.R
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.RideCancelledDialogFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.utils.AppUtils
import com.helloyatri.utils.extension.setDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideCancelledDialogFragment(private val callBack: () -> Unit) :
    BaseDialogFragment<RideCancelledDialogFragmentBinding>() {

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
    ): RideCancelledDialogFragmentBinding {
        return RideCancelledDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setUpViews()
        setClickListener()
    }

    private fun setUpViews() {
        successResult = arguments?.getString(STATUS).toString()
        apiViewModel?.popUp?.let {
            successTrip()
        } ?: run {
            successTrip()
        }
    }

    private fun successTrip() = with(binding) {
        textViewTitle.text =
            apiViewModel.popUp?.POPUPTITLE ?: getString(R.string.label_your_trip_is_verified)
        textViewDescription.text = apiViewModel.popUp?.POPUPMESSAGE ?: String.format(
            getString(R.string.label_dynamic_your_code_is_match_with_pooja_start_your_ride_now),
            firstName
        )
        imageViewResult.setDrawable(R.drawable.ic_failed_trip_verification)
//        textViewLetsRide.text =
//            apiViewModel.popUp?.POPUPBUTTONLBL ?: getString(R.string.label_okay_let_s_ride)
    }

    private fun setClickListener() = with(binding) {
        textViewLetsRide.setOnClickListener {
            callBack.invoke()
        }
    }

    companion object {
        const val STATUS = "STATUS"
        const val SUCCESS = "SUCCESS"
        const val FAILED = "FAILED"
        const val RIDE_VERIFIED = "RIDE_VERIFIED"
    }
}