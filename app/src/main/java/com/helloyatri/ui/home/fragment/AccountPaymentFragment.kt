package com.helloyatri.ui.home.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.R
import com.helloyatri.data.model.PaymentTab
import com.helloyatri.databinding.AccountPaymentFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AccountPaymentPagerAdapter
import com.helloyatri.ui.home.dialog.CalenderDialog
import com.helloyatri.utils.AppUtils.fairValue
import com.helloyatri.utils.Constants
import com.helloyatri.utils.DateUtils
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AccountPaymentFragment : BaseFragment<AccountPaymentFragmentBinding>() {

    private var accountPaymentPagerAdapter: AccountPaymentPagerAdapter? = null
    private val apiViewModel by activityViewModels<ApiViewModel>()
    private var currentPage = 0

    private val listTab by lazy {
        arrayListOf(
            PaymentTab(title = "Requested", "REQUESTED"),
            PaymentTab(title = "Accepted", status = "ACCEPTED"),
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountPaymentFragmentBinding {
        return AccountPaymentFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initObservers()
        setViewPager()
        setUpClickListener()
    }

    private fun initObservers() {
        apiViewModel.requestedPaymentTrips.observe(this) {
            it?.let {
                binding.apply {
                    if(DateUtils.isToday(apiViewModel.filterDate, DateUtils.DateFormat.YYYY_MM_DD)) {
                        textViewLabelTodayEarn.text = getString(R.string.label_today_earn)
                        textViewDateAndTime.text = it.formattedFilterDate.nullify()
                    } else {
                        textViewLabelTodayEarn.text = it.formattedFilterDate.nullify()
                        textViewDateAndTime.text = getString(R.string.label_total_you_earn)
                    }

                    textViewPrice.text = it.totalEarn
                    TextDecorator.decorate(
                        textViewRide, String.format(
                            getString(R.string.dummy_ride_n3_dynamic),
                            it.totalRides.fairValue("0")
                        )
                    )
                        .setTypeface(
                            R.font.lufga_medium,
                            it.totalRides.fairValue("0")
                        )
                        .setAbsoluteSize(
                            resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            it.totalRides.fairValue("0")
                        ).build()

                    TextDecorator.decorate(
                        textViewDistance, String.format(
                            getString(R.string.label_dynamic_distance_n105_5_km),
                            it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                        )
                    )
                        .setTypeface(
                            R.font.lufga_medium,
                            it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                        )
                        .setAbsoluteSize(
                            resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            it.totalDistance.nullify(Constants.DEFAULT_DISTANCE)
                        ).build()

                    TextDecorator.decorate(
                        textViewDuration, String.format(
                            getString(R.string.label_dynamic_duration_n02_40_hr),
                            it.totalDuration.nullify(Constants.DEFAULT_HOURS)
                        )
                    )
                        .setTypeface(
                            R.font.lufga_medium,
                            it.totalDuration.nullify(Constants.DEFAULT_HOURS)
                        )
                        .setAbsoluteSize(
                            resources.getDimensionPixelSize(com.intuit.ssp.R.dimen._14ssp),
                            it.totalDuration.nullify(Constants.DEFAULT_HOURS)
                        ).build()

                    if (currentPage == 1) {
                        textViewTotalRequested.text = getString(R.string.label_total_accepted_nfare_price)
                        textViewRequestedAmount.text = it.totalAcceptedFare.nullify(
                            Constants.DEFAULT_PRICE
                        )
                    } else {
                        textViewTotalRequested.text = getString(R.string.label_total_requested_nfare_price)
                        textViewRequestedAmount.text = it.totalRequestedFare.nullify(Constants.DEFAULT_PRICE
                        )
                    }

                }
            } ?: run {
                binding.apply {
                    textViewLabelTodayEarn.text = ""
                    textViewDateAndTime.text = ""
                    textViewPrice.text = getString(R.string.dummy_1_780)
                    textViewRide.text = getString(R.string.dummy_ride_n3)
                    textViewDistance.text = getString(R.string.dummy_distance_n105_5_km)
                    textViewDuration.text = getString(R.string.dummy_duration_n02_40_hr)
                    if (currentPage == 1) {
                        textViewTotalRequested.text =
                            getString(R.string.label_total_accepted_nfare_price)
                        textViewRequestedAmount.text = Constants.DEFAULT_PRICE
                    } else {
                        textViewTotalRequested.text =
                            getString(R.string.label_total_requested_nfare_price)
                        textViewRequestedAmount.text = Constants.DEFAULT_PRICE
                    }
                }
            }
        }
    }

    private fun setViewPager() = with(binding) {
        accountPaymentPagerAdapter =
            AccountPaymentPagerAdapter(this@AccountPaymentFragment, listTab)

        viewPager.apply {
            adapter = accountPaymentPagerAdapter
        }
        viewPager.isUserInputEnabled = true
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listTab[position].title
        }.attach()
    }

    private fun setUpClickListener() = with(binding) {
        imageViewBack.setOnClickListener {
            navigator.goBack()
        }

        imageViewCalendar.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                CalenderDialog {
                    onDateChanged(it)
                }.show(childFragmentManager, "")
            } else {
                showDatePickerDialog()
            }
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = context?.let {
            DatePickerDialog(it, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }.time
//                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val convTime = DateUtils.format(
                    selectedDate,
                    DateUtils.DateFormat.YYYY_MM_DD,
                    DateUtils.DateTimeZone.DEFAULT
                )
                onDateChanged(convTime)
            }, year, month, day)
        }

        datePickerDialog?.show()
    }

    private fun onDateChanged(convTime: String) {

        when (currentPage) {
            0 -> {
                val fragment = accountPaymentPagerAdapter?.fragmentList?.get(currentPage)
                if (fragment is AccountPaymentRequestedFragment) {
                    apiViewModel.filterDate = convTime
                    fragment.invokeApi(convTime)
                }
            }

            1 -> {
                val fragment = accountPaymentPagerAdapter?.fragmentList?.get(currentPage)
                if (fragment is AccountPaymentAcceptedFragment) {
                    apiViewModel.filterDate = convTime
                    fragment.invokeApi(convTime)
                }
            }
        }
    }
}