package com.helloyatri.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.DriverStatusResponse
import com.helloyatri.data.model.LoginResponse
import com.helloyatri.data.model.OTPVerificationResponse
import com.helloyatri.databinding.SplashActivityBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.auth.fragment.DriverVerificationFragment
import com.helloyatri.ui.auth.fragment.ForgotPasswordFragment
import com.helloyatri.ui.auth.fragment.LoginFragment
import com.helloyatri.ui.auth.fragment.OTPVerificationFragment
import com.helloyatri.ui.auth.fragment.ResetPasswordFragment
import com.helloyatri.ui.auth.fragment.SignUpFragment
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.clear

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var splashActivityBinding: SplashActivityBinding
    private val apiViewModel by viewModels<ApiViewModel>()

    override fun findFragmentPlaceHolder(): Int {
        return 0
    }

    override fun createViewBinding(): View {
        splashActivityBinding = SplashActivityBinding.inflate(layoutInflater)
        return splashActivityBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary), false
        )
        initObservers()
        if (appSession.isInitial) {
            appSession.isLoggedIn = appSession.user?.token?.trim()?.isNotEmpty() ?: false
            if (appSession.isLoggedIn == true) {
                getDriverStatus()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
//                    when {
//                        appSession.isDriverVerified && appSession.isLoggedIn == true -> {
//                            loadActivity(DriverDocumentsActivity::class.java).byFinishingCurrent()
//                                .start()
//                        }
//
//                        appSession.isDriverVerified && appSession.isLoggedIn == false -> {
//                            loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
//                        }
//
//                        appSession.isLoggedIn == true -> {
//                            loadActivity(HomeActivity::class.java).byFinishingAll().start()
//                        }
//
//                        appSession.isLoggedIn == false -> {
//                            loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
//                        }
//
//                        !appSession.isDriverVerified || !appSession.isPersonalDetailsAdded || !appSession.isProfilePictureAdded || !appSession.isRequiredDocumentsAdded || !appSession.isDrivingLicenseAdded || !appSession.isGovernmentIDAdded || !appSession.isBankAccountDetailsAdded || !appSession.isAddVehicle || !appSession.isVehicleDocumentsAdded || !appSession.isVehiclePUCAdded || !appSession.isVehicleInsuranceAdded || !appSession.isVehicleRegistrationAdded || !appSession.isVehiclePermitAdded || !appSession.isVehiclePhotosAdded || !appSession.isVehicleFrontBackPhotoAdded || !appSession.isVehicleLeftRightPhotoAdded || !appSession.isVehicleChassisAdded || !appSession.isVehicleWithYourPhotoAdded -> {
//                            loadActivity(DriverDocumentsActivity::class.java).byFinishingCurrent()
//                                .start()
//                        }
//
//                        else -> {
//                            loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
//                        }
//                    }
                }, 3000)
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
            }, 3000)
        }
    }

    private fun getDriverStatus() {
        apiViewModel.getDriverStatus()
    }

    private fun initObservers() {
        apiViewModel.getDriverStatus.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {}
                    Status.SUCCESS -> {
                        it.data?.let { it ->
                            val response = Gson().fromJson(it, DriverStatusResponse::class.java)
                            response?.data?.let { driverStatus ->
                                driverStatus.addVehicle?.let {
                                    appSession.isAddVehicle = it.status ?: false
                                }
                                driverStatus.profileImage?.let {
                                    appSession.isProfilePictureAdded = it.status ?: false
                                }
                                driverStatus.profileInfo?.let {
                                    appSession.isPersonalDetailsAdded = it.status ?: false
                                }
                                driverStatus.requiredDocuments?.let {
                                    appSession.isRequiredDocumentsAdded = it.status ?: false
                                }
                                driverStatus.vehicleDocuments?.let {
                                    appSession.isVehicleDocumentsAdded = it.status ?: false
                                }
                                driverStatus.vehicleImages?.let {
                                    appSession.isVehiclePhotosAdded = it.status ?: false
                                }
                                driverStatus.verificationPending?.let {
                                    appSession.isDriverVerified = it.status ?: false
                                }
                                if (appSession.isAllDocumentUploaded()) {
                                    if (appSession.isDriverVerified && appSession.user?.status == 1) {
                                        loadActivity(HomeActivity::class.java).byFinishingAll()
                                            .start()
                                    } else {
                                        loadActivity(DriverDocumentsActivity::class.java).byFinishingAll()
                                            .start()
                                    }
                                } else {
                                    loadActivity(DriverDocumentsActivity::class.java).byFinishingAll()
                                        .start()
                                }
                            }

                        }
                    }

                    Status.ERROR -> {
                        val error =
                            resource.message?.let { it } ?: getString(resource.resId?.let { it }!!)
                        showErrorMessage(error)
                    }
                }
            }
        }
    }
}