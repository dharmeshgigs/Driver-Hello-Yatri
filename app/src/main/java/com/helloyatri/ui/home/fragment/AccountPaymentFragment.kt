package com.helloyatri.ui.home.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.helloyatri.data.model.PaymentTab
import com.helloyatri.data.model.TabTypeForPayment
import com.helloyatri.databinding.AccountPaymentFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AccountPaymentPagerAdapter
import com.helloyatri.ui.home.dialog.CalenderDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AccountPaymentFragment : BaseFragment<AccountPaymentFragmentBinding>() {

    private var accountPaymentPagerAdapter: AccountPaymentPagerAdapter? = null

    private val listTab by lazy {
        arrayListOf(
            PaymentTab(title = "Requested", status = TabTypeForPayment.REQUESTED),
            PaymentTab(title = "Accepted", status = TabTypeForPayment.ACCEPTED),
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        attachToRoot: Boolean
    ): AccountPaymentFragmentBinding {
        return AccountPaymentFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setViewPager()
        setUpClickListener()
    }

    private fun setViewPager() = with(binding) {
        accountPaymentPagerAdapter =
            AccountPaymentPagerAdapter(this@AccountPaymentFragment, listTab)

        viewPager.apply {
            adapter = accountPaymentPagerAdapter
        }
        viewPager.isUserInputEnabled = true
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
                CalenderDialog {}.show(childFragmentManager, "")
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

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                //         dateTextView.text = dateFormat.format(selectedDate)
            }, year, month, day)
        }

        datePickerDialog?.show()
    }
}