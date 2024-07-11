package com.helloyatri.ui.common.fieldselection.data

import com.google.gson.annotations.SerializedName

data class CommonFieldSelection(@SerializedName("name") var options: String? = null,

                                var isOptionSelected: Boolean = false)
