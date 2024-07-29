package com.helloyatri.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.gamingyards.sms.app.utils.Status
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DriverStatusResponse
import com.helloyatri.databinding.DriverDocumentsAcitivtyBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.ui.auth.fragment.DriverDocumentsFragment
import com.helloyatri.ui.auth.fragment.DriverPersonalDetailsFragment
import com.helloyatri.ui.auth.fragment.DriverPersonalProfilePictureFragment
import com.helloyatri.ui.auth.fragment.DriverRequiredDocumentsFragment
import com.helloyatri.ui.auth.fragment.DriverVehicleDetailsFragment
import com.helloyatri.ui.auth.fragment.DriverVerificationFragment
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.utils.Constants.ADD_VEHICLE
import com.helloyatri.utils.Constants.DRIVER_REQUIRED_DOCUMENT
import com.helloyatri.utils.Constants.UPDATE_PROFILE_INFO
import com.helloyatri.utils.Constants.UPDATE_PROFILE_PICTURE
import com.helloyatri.utils.Constants.VEHICLE_DOCUMENT
import com.helloyatri.utils.Constants.VEHICLE_PHOTO
import com.helloyatri.utils.Constants.VERIFICATION_PENDING
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDocumentsActivity : BaseActivity() {

    private lateinit var driverDocumentsActivityBinding: DriverDocumentsAcitivtyBinding
    private val apiViewModel by viewModels<ApiViewModel>()

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        driverDocumentsActivityBinding = DriverDocumentsAcitivtyBinding.inflate(layoutInflater)
        return driverDocumentsActivityBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        initObservers()
        if (appSession.isInitial && appSession.isAllDocumentUploaded()) {
            load(DriverVerificationFragment::class.java).clearHistory(null).replace(true)
        } else {
            load(DriverDocumentsFragment::class.java).clearHistory(null).replace(true)
        }

    }

    private fun initObservers() {
        apiViewModel.getDriverStatus.observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {

                    }

                    Status.SUCCESS -> {

                        it.data?.let {
                            val response =
                                Gson().fromJson(it, DriverStatusResponse::class.java)
                            when (response.code) {
                                UPDATE_PROFILE_INFO.toInt() -> {
                                    load(DriverPersonalDetailsFragment::class.java).replace(false)
                                }

                                UPDATE_PROFILE_PICTURE.toInt() -> {
                                    load(DriverPersonalProfilePictureFragment::class.java)
                                        .setBundle(
                                            DriverPersonalProfilePictureFragment.createBundle(
                                                statusCode = UPDATE_PROFILE_PICTURE
                                            )
                                        )
                                        .replace(false)
                                }

                                DRIVER_REQUIRED_DOCUMENT.toInt() -> {
                                    load(DriverRequiredDocumentsFragment::class.java)
                                        .setBundle(
                                            DriverRequiredDocumentsFragment.createBundle(
                                                statusCode = DRIVER_REQUIRED_DOCUMENT
                                            )
                                        )
                                        .replace(false)
                                }

                                ADD_VEHICLE.toInt() -> {
                                    load(DriverVehicleDetailsFragment::class.java).replace(true)
                                }

                                VEHICLE_DOCUMENT.toInt() -> {
                                    load(DriverRequiredDocumentsFragment::class.java)
                                        .setBundle(
                                            DriverRequiredDocumentsFragment.createBundle(
                                                statusCode = VEHICLE_DOCUMENT
                                            )
                                        )
                                        .replace(false)
                                }

                                VEHICLE_PHOTO.toInt() -> {
                                    load(DriverRequiredDocumentsFragment::class.java)
                                        .setBundle(
                                            DriverRequiredDocumentsFragment.createBundle(
                                                statusCode = VEHICLE_PHOTO
                                            )
                                        )
                                        .replace(false)
                                }

                                VEHICLE_PHOTO.toInt() -> {
                                    load(DriverRequiredDocumentsFragment::class.java)
                                        .setBundle(
                                            DriverRequiredDocumentsFragment.createBundle(
                                                statusCode = VEHICLE_PHOTO
                                            )
                                        )
                                        .replace(false)
                                }

                                VERIFICATION_PENDING.toInt() -> {
                                    load(DriverVerificationFragment::class.java)
                                        .replace(false)
                                }

//                                VERIFICATION_COMPLETED.toInt() -> {
//                                    if (session.user?.status == 1){
//                                        loadActivity(HomeActivity::class.java)
//                                            .byFinishingAll()
//                                            .start()
//                                    }
//                                }


//                                else -> {
//                                    response?.data?.let { driverStatus ->
//                                        driverStatus.addVehicle?.let {
//                                            session.isAddVehicle = it.status ?: false
//                                        }
//                                        driverStatus.profileImage?.let {
//                                            session.isProfilePictureAdded = it.status ?: false
//                                        }
//                                        driverStatus.profileInfo?.let {
//                                            session.isPersonalDetailsAdded = it.status ?: false
//                                        }
//                                        driverStatus.requiredDocuments?.let {
//                                            session.isRequiredDocumentsAdded = it.status ?: false
//                                        }
//                                        driverStatus.vehicleDocuments?.let {
//                                            session.isVehicleDocumentsAdded = it.status ?: false
//                                        }
//                                        driverStatus.vehicleImages?.let {
//                                            session.isVehiclePhotosAdded = it.status ?: false
//                                        }
//                                        driverStatus.verificationPending?.let {
//                                            session.isDriverVerified = it.status ?: false
//                                        }
//                                        if (getSourceScreen == SignUpFragment::class.java.simpleName || getSourceScreen == LoginFragment::class.java.simpleName) {
//                        //                                    navigator.loadActivity(DriverDocumentsActivity::class.java)
//                        //                                        .byFinishingAll()
//                        //                                        .start()
//                                            if (session.isAllDocumentUploaded()) {
//
//                                                if (session.isDriverVerified && session.user?.status == 1) {
//                                                    navigator.loadActivity(HomeActivity::class.java)
//                                                        .byFinishingAll()
//                                                        .start()
//                                                } else {
//                                                    navigator.load(DriverVerificationFragment::class.java)
//                                                        .replace(false)
//                                                }
//                                            } else {
//                        //                                        navigator.loadActivity(DriverDocumentsActivity::class.java)
//                        //                                            .byFinishingAll()
//                        //                                            .start()
//                                            }
//                                        } else if (getSourceScreen == ForgotPasswordFragment::class.java.simpleName) {
//                                            navigator.load(ResetPasswordFragment::class.java)
//                                                .replace(false)
//                                        }
//                                    } ?: run {
//                                        showSomethingMessage()
//                                    }
//                                }

                            }

                        }
                    }

                    Status.ERROR -> {
//                        hideLoader()
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }
                }
            }
        }
    }


    private fun setUpToolbar() = with(driverDocumentsActivityBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }

    private fun getDriverStatus() {
        apiViewModel.getDriverStatus()
    }

    override fun onResume() {
        getDriverStatus()
        super.onResume()
        Log.i("TAG", "onResume: ")
    }

}