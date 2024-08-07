package com.helloyatri.ui.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

import com.helloyatri.R
import com.helloyatri.core.Session
import com.helloyatri.di.App
import com.helloyatri.exception.ApplicationException
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.activity.SplashActivity
import com.helloyatri.ui.base.loader.LoadingDialog
import com.helloyatri.ui.manager.ActivityBuilder
import com.helloyatri.ui.manager.ActivityStarter
import com.helloyatri.ui.manager.FragmentActionPerformer
import com.helloyatri.ui.manager.FragmentNavigationFactory
import com.helloyatri.ui.manager.Navigator
import com.helloyatri.utils.fileselector.MediaSelectHelper
import com.helloyatri.utils.hideView
import com.helloyatri.utils.showView
import com.helloyatri.utils.textdecorator.TextDecorator
import com.helloyatri.utils.toolbar.CustomToolbar
import com.helloyatri.utils.toolbar.MenuItem
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : PusherActivity(), HasToolbar, Navigator {

    @Inject
    lateinit var navigationFactory: FragmentNavigationFactory

    @Inject
    lateinit var activityStarter: ActivityStarter

    @Inject
    lateinit var mediaSelectHelper: MediaSelectHelper

    private var toolbar: CustomToolbar? = null
    internal var progressDialog: ProgressDialog? = null
    internal var alertDialog: AlertDialog? = null


    private val view by lazy {
        createViewBinding()
    }

    private val loader by lazy {
        LoadingDialog(this) {
            goBack()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(createViewBinding())
        createFirebaseToken()/*if (toolbar != null)
            setSupportActionBar(toolbar)*/

        setUpAlertDialog()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        setContentView(view)
    }

    private fun setUpAlertDialog() {
        alertDialog =
            AlertDialog.Builder(this).setPositiveButton("ok", null).setTitle(R.string.app_name)
                .create()
    }

    fun <F : BaseFragment<*>> getCurrentFragment(): F? {
        return if (findFragmentPlaceHolder() == 0) null else supportFragmentManager.findFragmentById(
            findFragmentPlaceHolder()
        ) as F?
    }

    abstract fun findFragmentPlaceHolder(): Int

    abstract fun createViewBinding(): View


    fun showErrorMessage(message: String?) {/*val f = getCurrentFragment<BaseFragment<*, *>>()
        if (f != null)
            Snackbar.make(f.getView()!!, message!!, BaseTransientBottomBar.LENGTH_SHORT).show()*/

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun toggleLoader(show: Boolean) {

        if (show) {
            if (!progressDialog!!.isShowing) progressDialog!!.show()
        } else {
            if (progressDialog!!.isShowing) progressDialog!!.dismiss()
        }
    }

    protected fun shouldGoBack(): Boolean {
        return true
    }

    override fun onBackPressed() {
        hideKeyboard()
        val currentFragment = getCurrentFragment<BaseFragment<*>>()
        if (currentFragment == null) super.onBackPressed()
        else if (currentFragment.onBackActionPerform() && shouldGoBack()) {
            if (supportFragmentManager.backStackEntryCount > 1) {
                super.onBackPressed()
            } else {
                finish()
            }
        }

        // pending animation
        // overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);

    }

    fun hideKeyboard() {
        // Check if no view has focus:

        val view = this.currentFocus
        if (view != null) {
            val inputManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setToolbar(toolbar: CustomToolbar) {
        this.toolbar = toolbar
    }

    override fun showToolbar(isVisible: Boolean): HasToolbar {
        toolbar?.let { toolbar ->
            if (isVisible) showView(toolbar)
            else hideView(toolbar)

            toolbar.reset()
        }

        return this
    }

    override fun setToolbarTitle(title: String): HasToolbar {
        toolbar?.setToolbarTitle(title)

        return this
    }


    override fun setToolbarTitle(@StringRes title: Int): HasToolbar {
        toolbar?.setToolbarTitle(title)

        return this
    }

    override fun setShortNameTitle(title: String): HasToolbar {
        toolbar?.setShortNameTitle(title)

        return this
    }

    override fun showBackButton(isVisible: Boolean): HasToolbar {
        toolbar?.apply {
            showBackButton(isVisible)
            setOnBackButtonClickListener {
                goBack()
            }
        }

        return this
    }

    override fun setBackButtonIcon(@DrawableRes backButtonIcon: Int): HasToolbar {
        toolbar?.setBackButtonIcon(backButtonIcon)

        return this
    }

    override fun showHamburgerIcon(isVisible: Boolean): HasToolbar {
        toolbar?.showHamburgerIcon(isVisible)
        return this
    }

    override fun setOnHamburgerIconClickListener(onMenuIconClick: () -> Unit) {
        toolbar?.setOnHamburgerIconClickListener(onMenuIconClick)
    }


    override fun setOnShortNameClickListener(onShortNameClick: () -> Unit) {
        toolbar?.setOnShortNameClickListener(onShortNameClick)
    }

    override fun setHamburgerIcon(@DrawableRes hamburgerIcon: Int): HasToolbar {
        toolbar?.setHamburgerIcon(hamburgerIcon)

        return this
    }

    override fun setToolbarColor(@ColorRes color: Int): HasToolbar {
        toolbar?.setToolbarColor(color)
        return this
    }

    override fun setToolbarTitleColor(color: Int): HasToolbar {
        toolbar?.setToolbarTitleColor(color)
        return this
    }

    /**
     * This method is use to set title on the toolbar and then it will decorate the title with the help of [TextDecorator]
     *
     * Note: It has higher priority than [setToolbarTitle].
     *
     * @param title Title which you want to set on toolbar
     * @param decorateToolbarTitle It will give you [TextDecorator] instance. Call the appropriate method on it.
     * Do not call build method on it, it will be called automatically.
     * @see TextDecorator
     * */
    override fun setAndDecorateToolbarTitle(
        title: String,
        decorateToolbarTitle: (textDecorator: TextDecorator) -> TextDecorator
    ): HasToolbar {
        toolbar?.setAndDecorateToolbarTitle(title, decorateToolbarTitle)

        return this
    }

    override fun addMenuItems(vararg menuItems: MenuItem): HasToolbar {
        toolbar?.addMenuItems(*menuItems)

        return this
    }

    override fun updateMenuItem(menuItem: MenuItem, predicate: (MenuItem) -> Boolean): HasToolbar {
        toolbar?.updateMenuItem(menuItem = menuItem, predicate = predicate)

        return this
    }

    override fun updateMenuItem(
        predicate: (MenuItem) -> Boolean,
        menuItemToUpdate: (MenuItem) -> Unit
    ): HasToolbar {
        toolbar?.updateMenuItem(predicate = predicate, menuItemToUpdate = menuItemToUpdate)

        return this
    }

    override fun setOnMenuItemClickListener(onMenuItemClick: (menuItem: MenuItem) -> Unit) {
        toolbar?.setOnMenuItemClickListener(onMenuItemClick)
    }

    override fun setMoreOptionIcon(moreOptionIcon: Int): HasToolbar {
        toolbar?.setMoreOptionIcon(moreOptionIcon)

        return this
    }

    override fun setMaxItemAllowed(maxItemAllowed: Int): HasToolbar {
        toolbar?.setMaxItemAllowed(maxItemAllowed)

        return this
    }

    override fun setPopupMenuElevation(@DimenRes popupMenuElevation: Int): HasToolbar {
        toolbar?.setPopupMenuElevation(popupMenuElevation)

        return this
    }

    override fun setPopupMenuCornerRadius(@DimenRes popupMenuCornerRadius: Int): HasToolbar {
        toolbar?.setPopupMenuCornerRadius(popupMenuCornerRadius)

        return this
    }

    override fun setPopupMenuBackgroundColor(popupMenuBackgroundColor: Int): HasToolbar {
        toolbar?.setPopupMenuBackgroundColor(popupMenuBackgroundColor)

        return this
    }

    override fun setToolbarElevation(elevation: Int): HasToolbar {
        toolbar?.setToolbarElevation(elevation)

        return this
    }

    override fun build() {
        toolbar?.build()
    }

    fun showKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val inputManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    override fun <T : BaseFragment<*>> load(tClass: Class<T>): FragmentActionPerformer<T> {
        return navigationFactory.make(tClass)
    }

    override fun loadActivity(aClass: Class<out BaseActivity>): ActivityBuilder {
        return activityStarter.make(aClass)
    }

    override fun <T : BaseFragment<*>> loadActivity(
        aClass: Class<out BaseActivity>,
        pageTClass: Class<T>
    ): ActivityBuilder {
        return activityStarter.make(aClass).setPage(pageTClass)
    }


    override fun goBack() {
        onBackPressed()
    }

    fun showMessage(message: String) {
        showSnackBar(message)

    }

    fun showSuccessMessage(message: String) {
        showSuccessSnackBar(message)

    }

    fun showMessage(@StringRes stringId: Int) {
        showSnackBar(getString(stringId))
    }

    fun showMessage(applicationException: ApplicationException) {

        showSnackBar(applicationException.message)
    }

    private fun showSnackBar(message: String) {
        hideKeyboard()
        if (view != null) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.duration = 2000

            snackbar.setActionTextColor(Color.WHITE)
            //snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }

    private fun showSuccessSnackBar(message: String) {
        hideKeyboard()
        if (view != null) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            snackbar.duration = 2000

            snackbar.setActionTextColor(Color.WHITE)
            //snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }


    // logout
    fun logout() {
        appSession.isInitial = false
        appSession.user = null
        appSession.isLoggedIn = false
        appSession.clearSession()
        loadActivity(AuthActivity::class.java).byFinishingAll().start()
    }


    private fun createFirebaseToken() {
        /*try {
            Log.e("FCM_TOKEN", "TRY")
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                Log.e("FCM_TOKEN SUCCESS", task.isSuccessful.toString())
                if (!task.isSuccessful) {
                    Log.e("FCM_TOKEN", task.isSuccessful.toString())
                    return@OnCompleteListener
                }
                //Get new Instance ID token
                val token = task.result
                Log.e("FCM_TOKEN", token)
                try {
                    Log.e("FCM_TOKEN TRY", token)

                    appSession.deviceToken = token
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
        } catch (e: Exception) {
            Log.e("FCM_TOKEN", "CATCH")
            e.printStackTrace()
        }*/
    }


     fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, "Driver_hello_yatri")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android 8.0 and above, create a NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Driver_hello_yatri",
                "Driver_hello_yatri",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


}
