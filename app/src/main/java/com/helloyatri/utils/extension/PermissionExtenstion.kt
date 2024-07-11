package com.helloyatri.utils.extension

import androidx.fragment.app.Fragment
import com.helloyatri.R

fun Fragment.commandDialogYesNo(title: String, message: String, callback: (Int) -> Unit) {

    try {
        val alertDialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Hello Yatri")
        alertDialog.setMessage("Please allow access.")

        alertDialog.setPositiveButton(resources.getString(R.string.btn_ok)) { dialog, which ->
            callback(1)
        }
        alertDialog.setNegativeButton(resources.getString(R.string.btn_cancel)) { dialog, which ->
            dialog.dismiss()
            callback(0)
        }
        alertDialog.show()
    } catch (e: Exception) {

    }

}