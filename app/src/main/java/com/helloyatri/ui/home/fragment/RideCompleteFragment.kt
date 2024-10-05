package com.helloyatri.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.helloyatri.R
import com.helloyatri.data.Request
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.RideCompleteFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.CANCEL_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.ICON
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.OK_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.TITLE
import com.helloyatri.utils.AppUtils.openCallDialer
import com.helloyatri.utils.extension.enableTextView
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideCompleteFragment : BaseFragment<RideCompleteFragmentBinding>() {

    private val apiViewModel: ApiViewModel by activityViewModels()
    private var tripRiderModel: TripRiderModel? = null
    private var name: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tripRiderModel = apiViewModel.tripRequest.value
        name = tripRiderModel?.riderDetails?.name ?: ""
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): RideCompleteFragmentBinding {
        return RideCompleteFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initViews()
        initObservers()
        setClickListener()
    }

    private fun initObservers() = with(binding) {
        apiViewModel.paymentCollectedLiveData.observe(requireActivity()) {
            it?.tripDetails?.let {
                it.tip_amount_txt?.let {
                    TextDecorator.decorate(
                        textViewCustomerSetTip, String.format(
                            getString(R.string.dummy_dynamic_customer_set_70_tip_for_you), it
                        )
                    ).build()
                }
                it.total_amount_txt?.let {
                    textViewPrice.text = it

                }
                it.payment_collect_note?.let {
                    textViewYouHaveToCollect.text = it
                }
                binding.textViewCustomerSetTip.show()
                binding.constraintYouHaveCollect.show()
                textViewCollected.isEnabled = true
                textViewCollected.isClickable = true
                textViewCollected.enableTextView(true)
            }
        }

        apiViewModel.collectPaymentLiveData.observe(requireActivity()) { resource ->
            resource.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        navigator.loadActivity(HomeActivity::class.java)
                            .byFinishingAll()
                            .start()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }

                    Status.LOADING -> {
                        showLoader()
                    }
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        textViewCollected.isEnabled = false
        textViewCollected.isClickable = false
        textViewCollected.enableTextView(false)
        tripRiderModel?.let {
            it.riderDetails?.let {
                it.name?.let {
                    textViewSubTitle.text = String.format(
                        getString(R.string.label_dynamic_you_have_drop_poojas_at_their_destination_nsee_you_on_the_next_trip),
                        it
                    )
                    textViewUserName.text = it
                }
                imageViewUserProfile.loadImageFromServerWithPlaceHolder(it.profile)
            }
            it.tripDetails?.let {
                it.startLocation?.address?.let {
                    textViewStartLocation.text = it
                }
                it.endLocation?.address?.let {
                    textViewDestinationLocation.text = it
                }

                it.distanceTxt?.let {
                    TextDecorator.decorate(
                        textViewDistance, String.format(
                            getString(R.string.label_dynamic_distance_n105_5_km), it
                        )
                    ).setTypeface(
                        R.font.lufga_semi_bold, it
                    ).setAbsoluteSize(
                        resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                    ).build()
                }

                it.durationTxt?.let {
                    TextDecorator.decorate(
                        textViewDuration, String.format(
                            getString(R.string.label_dynamic_duration_n02_40_hr), it
                        )
                    ).setTypeface(
                        R.font.lufga_semi_bold, it
                    ).setAbsoluteSize(
                        resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                    ).build()
                }

                it.estimatedFare?.let {
                    TextDecorator.decorate(
                        textViewFairPrice, String.format(
                            getString(R.string.label_dynamic_fare_price_n_780), it
                        )
                    ).setTypeface(R.font.lufga_semi_bold, getString(R.string.label_currency))
                        .setAbsoluteSize(
                            resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            getString(R.string.label_currency)
                        ).setTypeface(
                            R.font.lufga_semi_bold, it
                        ).setAbsoluteSize(
                            resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp), it
                        ).build()
                }

                it.estimatedFare?.let {
                    textViewPrice.text = getString(R.string.label_currency).plus(" ").plus(it)
                } ?: run {
                    constraintYouHaveCollect.hide()
                }

                it.tip_amount_txt?.let {
                    TextDecorator.decorate(
                        textViewCustomerSetTip, String.format(
                            getString(R.string.dummy_dynamic_customer_set_70_tip_for_you), it
                        )
                    ).build()
                    binding.textViewCustomerSetTip.show()
                }?:  kotlin.run {
                    binding.textViewCustomerSetTip.hide()
                }
            }
        }

        binding.textViewNote.text = ""
        apiViewModel.pickupNoteLiveData.value?.let {
            if (it.isNotEmpty()) {
                TextDecorator.decorate(
                    binding.textViewNote, String.format(
                        getString(R.string.label_dynamic_note_message_show_here), it
                    )
                ).setTextColor(R.color.colorPrimary, getString(R.string.label_note)).build()
            }
        }

    }

    private fun setClickListener() = with(binding) {
        textViewCollected.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == CommonYesNoDialogFragment.YES) {
                    apiViewModel.collectTripPayment(
                        Request(
                            trip_id = tripRiderModel?.tripDetails?.id?.toString() ?: ""
                        )
                    )
                }
            }

            commonYesNoDialogFragment.arguments = bundleOf(
                TITLE to String.format(
                    getString(R.string.label_dynamic_have_you_collected_money_from_to_pooja), name
                ),
                CANCEL_TEXT to getString(R.string.btn_no_still_not),
                OK_TEXT to getString(R.string.label_yes_collected),
                ICON to R.drawable.image_money_collect
            )

            commonYesNoDialogFragment.show(
                childFragmentManager, PickUpSpotFragment::class.java.simpleName
            )
        }

        imageViewCall.setOnClickListener {
// TODO: Set Phone Number Dynamic
            tripRiderModel?.riderDetails?.mobile?.let {
                activity?.openCallDialer(it)
            }
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}