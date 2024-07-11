package com.helloyatri.ui.home.sidemenu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SideMenuTag : Parcelable {
    EDIT_PROFILE, RIDE_ACTIVITY, PAYMENT, SAVED_ADDRESS, PREFERENCES, HELP_CENTER, DOCUMENTS, LOGOUT
}