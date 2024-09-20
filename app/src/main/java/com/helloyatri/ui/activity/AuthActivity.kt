package com.helloyatri.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.databinding.AuthAcitivtyBinding
import com.helloyatri.ui.auth.fragment.LoginFragment
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.tutorial.fragment.AuthTutorialFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {

    private lateinit var authAcitivtyBinding: AuthAcitivtyBinding
    private lateinit var activityResultLauncherCamera: ActivityResultLauncher<Array<String>>
    private val permissionList = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        authAcitivtyBinding = AuthAcitivtyBinding.inflate(layoutInflater)
        return authAcitivtyBinding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        if (appSession.isInitial) {
            load(LoginFragment::class.java).replace(false)
        } else {
            load(AuthTutorialFragment::class.java).replace(false)
        }
        activityResultLauncherCamera =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { grantResults ->
            }
    }

    private fun setUpToolbar() = with(authAcitivtyBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }

    override fun onResume() {
        super.onResume()
        notificationPermission()
    }

    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncherCamera.launch(permissionList)
        }
    }

}