package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.helloyatri.R
import com.helloyatri.databinding.ConfirmStartRidePointFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfirmStartRidePointFragment : BaseFragment<ConfirmStartRidePointFragmentBinding>() {

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): ConfirmStartRidePointFragmentBinding {
        return ConfirmStartRidePointFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        loadLocation()
    }

    private fun loadLocation() = with(binding) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(900)
            editTextLocation.setText(getString(R.string.hint_ex_101_national_san_bruno_ca_94580))
        }

        btnConfirmPoint.setOnClickListener {
            navigator.goBack()
        }
    }

    override fun setUpToolbar() = with(binding.customToolbar) {
        showBackButton(true)
        setToolbarTitle(getString(R.string.title_start_ride_from)).setToolbarColor(
                android.R.color.transparent).build()
    }
}