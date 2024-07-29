package com.helloyatri.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.SplashActivityBinding
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.utils.extension.changeStatusBarColor

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private lateinit var splashActivityBinding: SplashActivityBinding

    override fun findFragmentPlaceHolder(): Int {
        return 0
    }

    override fun createViewBinding(): View {
        splashActivityBinding = SplashActivityBinding.inflate(layoutInflater)
        return splashActivityBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary),
                false)

        appSession.isLoggedIn = appSession.user?.token?.trim()?.isNotEmpty() ?: false
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                appSession.isDriverVerified && appSession.isLoggedIn == true -> {
                    loadActivity(DriverDocumentsActivity::class.java).byFinishingCurrent().start()
                }

                appSession.isDriverVerified && appSession.isLoggedIn == false -> {
                    loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
                }

                appSession.isLoggedIn == true -> {
                    loadActivity(HomeActivity::class.java).byFinishingAll().start()
                }

                appSession.isLoggedIn == false -> {
                    loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
                }

                !appSession.isDriverVerified || !appSession.isPersonalDetailsAdded || !appSession.isProfilePictureAdded || !appSession.isRequiredDocumentsAdded || !appSession.isDrivingLicenseAdded || !appSession.isGovernmentIDAdded || !appSession.isBankAccountDetailsAdded || !appSession.isAddVehicle || !appSession.isVehicleDocumentsAdded || !appSession.isVehiclePUCAdded || !appSession.isVehicleInsuranceAdded || !appSession.isVehicleRegistrationAdded || !appSession.isVehiclePermitAdded || !appSession.isVehiclePhotosAdded || !appSession.isVehicleFrontBackPhotoAdded || !appSession.isVehicleLeftRightPhotoAdded || !appSession.isVehicleChassisAdded || !appSession.isVehicleWithYourPhotoAdded -> {
                    loadActivity(DriverDocumentsActivity::class.java).byFinishingCurrent().start()
                }

                else -> {
                    loadActivity(AuthActivity::class.java).byFinishingCurrent().start()
                }
            }
        }, 3000)
    }
}