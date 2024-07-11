package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.helloyatri.R
import com.helloyatri.databinding.FragmentPickUpSpotBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.bottomsheet.CancelRideBottomSheet
import com.helloyatri.ui.home.bottomsheet.EmergencyAssistanceBottomSheet
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.CANCEL_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.ICON
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.OK_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.TITLE
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.YES
import com.helloyatri.ui.home.dialog.RideVerificationDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment
import com.helloyatri.ui.home.dialog.RideVerificationResultDialogFragment.Companion.SUCCESS
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class PickUpSpotFragment : BaseFragment<FragmentPickUpSpotBinding>() {
    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): FragmentPickUpSpotBinding {
        return FragmentPickUpSpotBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun bindData() {
        setTextDecorator()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        textViewArrivedAtPoint.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment { action ->
                if (action == YES) {
                    RideVerificationDialogFragment {
                        val rideVerificationResultDialogFragment =
                                RideVerificationResultDialogFragment()
                        if (it == SUCCESS) {
                            rideVerificationResultDialogFragment.arguments = bundleOf(
                                    RideVerificationResultDialogFragment.STATUS to SUCCESS,
                            )

                            rideVerificationResultDialogFragment.show(childFragmentManager,
                                    PickUpSpotFragment::class.java.simpleName)
                        } else {
                            rideVerificationResultDialogFragment.arguments = bundleOf(
                                    RideVerificationResultDialogFragment.STATUS to RideVerificationResultDialogFragment.FAILED,
                            )
                            rideVerificationResultDialogFragment.show(childFragmentManager,
                                    PickUpSpotFragment::class.java.simpleName)
                        }
                    }.show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
                }
            }

            commonYesNoDialogFragment.arguments = bundleOf(
                    TITLE to getString(R.string.label_have_you_arrived_to_pooja_s_pickup_point),
                    CANCEL_TEXT to getString(R.string.btn_no_still_not),
                    OK_TEXT to getString(R.string.btn_yes_i_reached),
                    ICON to R.drawable.ic_car_location)

            commonYesNoDialogFragment.show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }

        textViewCancelRide.setOnClickListener {
            CancelRideBottomSheet {
                activity?.apply {
                    finish()
                }
            }.show(childFragmentManager, PickUpSpotFragment::class.java.simpleName)
        }

        textViewEmergency.setOnClickListener {
            EmergencyAssistanceBottomSheet().show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }

        textViewEndTrip.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == YES) {
                    navigator.load(RideCompleteFragment::class.java).replace(false)
                }
            }
            commonYesNoDialogFragment.arguments = bundleOf(
                    TITLE to getString(R.string.label_have_you_arrived_to_pooja_s_pickup_point),
                    CANCEL_TEXT to getString(R.string.btn_no_still_not),
                    OK_TEXT to getString(R.string.btn_yes_i_reached),
                    ICON to R.drawable.ic_car_location)

            commonYesNoDialogFragment.show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewNote, textViewNote.trimmedText)
                .setTextColor(R.color.homeBgBlueColor, "Note:").build()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRideApproved(status: String) {
        showLoader()
        activity?.runOnUiThread {
            requireActivity().runCatching {
                setUpApprovedRideUI()
            }
        }
    }

    private fun setUpApprovedRideUI() = with(binding) {
        hideLoader()
        textViewLabelReachIn.text = getString(R.string.label_reaching_destination)
        textViewMeetDriverAt.text = getString(R.string.label_you_will_reach_at_approx_10_00pm)
        textViewEstimatedTime.text = getString(R.string.label_15_min)
        layoutRideDetails.hide()
        constraintLayoutApprovedRide.show()

    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}