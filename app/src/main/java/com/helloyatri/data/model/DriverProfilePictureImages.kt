package com.helloyatri.data.model

import android.graphics.Bitmap

data class DriverProfilePictureImages(
        var images: String? = null,
        var imageBitmap: Bitmap? = null,
        var local: Boolean = false,
        var id:String? =null,
        var status:String? =null,
        var rejection_reason:String? =null,
)
