package com.helloyatri.ui.home.dialog

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.helloyatri.databinding.CalenderDialogBinding
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.ui.home.compose.Example1Page
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CalenderDialog(private val callBack: (action: String) -> Unit) :
        BaseDialogFragment<CalenderDialogBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): CalenderDialogBinding {
        return CalenderDialogBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        fun getCurrentDateFormatted(): String {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            return dateFormat.format(Date())
        }
        binding.textViewDate.text = getCurrentDateFormatted()
        isCancelable = true
        binding.composeView.setContent {
            Example1Page {
                binding.textViewDate.text = formatDate(it.date.toString())
            }
        }
        setClickListener()
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    companion object {
        const val YES = "YES"
        const val NO = "NO"
    }

    private fun setClickListener() = with(binding) {
        buttonUpdate.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }
}