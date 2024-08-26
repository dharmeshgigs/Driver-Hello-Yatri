package com.helloyatri.ui.home.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.databinding.IntercityRideRequestFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.IntercityRideMainAdapter
import com.helloyatri.ui.home.dialog.CalenderDialog
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.getScheduleRideList
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class IntercityRideFragment : BaseFragment<IntercityRideRequestFragmentBinding>() {

    private val intercityRideMainAdapter by lazy {
        IntercityRideMainAdapter()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): IntercityRideRequestFragmentBinding {
        return IntercityRideRequestFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        binding.textViewMarkAllRead.hide()
        setAdapter()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        intercityRideMainAdapter.setOnAcceptOrDeclineClickListener { item, subItem, action ->
        }

        imageViewBack.setOnClickListener {
            navigator.goBack()
        }

        imageViewCalendar.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                CalenderDialog {}.show(childFragmentManager, "")
            } else {
                showDatePickerDialog()
            }
            //CalenderDialog {}.show(childFragmentManager, "")
        }
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


    private fun setAdapter() = with(binding) {
        recyclerView.apply {
            adapter = intercityRideMainAdapter
            intercityRideMainAdapter.setItems(requireActivity().getScheduleRideList(), 1)
        }
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}