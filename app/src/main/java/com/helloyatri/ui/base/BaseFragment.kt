package com.helloyatri.ui.base

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.helloyatri.R
import com.helloyatri.core.Session
import com.helloyatri.exception.ApplicationException
import com.helloyatri.exception.AuthenticationException
import com.helloyatri.exception.ServerException
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.base.loader.Loader
import com.helloyatri.ui.manager.Navigator
import com.helloyatri.utils.Validator
import com.helloyatri.utils.location.LocationProvider
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.Locale
import javax.inject.Inject

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    protected lateinit var toolbar: HasToolbar

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var validator: Validator

    lateinit var session: Session

    private var _binding: T? = null

    protected val binding: T
        get() = _binding!!

    private lateinit var loader: Loader
    private var locationProvider : LocationProvider? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createViewBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        bindData()
    }

    fun showLoader() {
        navigator.toggleLoader(true)
    }

    fun hideLoader() {
        navigator.toggleLoader(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is HasToolbar) toolbar = activity as HasToolbar
        if (activity is Loader) loader = activity as Loader
        if (activity is BaseActivity) {
            session = (activity as BaseActivity).appSession
        }
    }


    fun hideKeyBoard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).hideKeyboard()
        }
    }

    fun showKeyBoard() {
        if (activity is BaseActivity) {
            (activity as BaseActivity).showKeyboard()
        }
    }


    fun <T : BaseFragment<*>> getParentFragment(targetFragment: Class<T>): T? {
        if (parentFragment == null) return null
        try {
            return targetFragment.cast(parentFragment)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
        return null
    }


    open fun onShow() {

    }

    fun showMessage(message: String) {
        showSnackBar(message)
    }

    fun showSuccessMessage(message: String) {
        showSuccessSnackBar(message)
    }

    fun showErrorMessage(message: String) {
        showErrorSnackBar(message)
    }

    fun showMessage(@StringRes stringId: Int) {
        showSnackBar(getString(stringId))
    }

    fun showMessage(applicationException: ApplicationException) {
        showSnackBar(applicationException.message)
    }

    private fun showSnackBar(message: String) {
        hideKeyBoard()
        if (view != null) {
            val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            snackbar.duration = 3000

            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(requireActivity().resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }

    private fun showSuccessSnackBar(message: String) {
        hideKeyBoard()
        if (view != null) {
            val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            snackbar.duration = 3000

            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(requireActivity().resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }

    private fun showSnackBar(message: String, viewSet: View) {
        hideKeyBoard()
        if (viewSet != null) {
            val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            snackbar.duration = 3000

            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(requireActivity().resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }


    open fun onBackActionPerform(): Boolean {
        return true
    }

    open fun onViewClick(view: View) {

    }

    fun onError(throwable: Throwable) {
        hideLoader()
        try {
            when (throwable) {
                is ServerException -> showMessage(throwable.message.toString())
                is ConnectException -> showMessage(R.string.connection_exception)
                is AuthenticationException -> {
                    logout()
                }

                is ApplicationException -> {
                    showMessage(throwable.toString())
                }

                is SocketTimeoutException -> showMessage(R.string.socket_time_out_exception)
                else -> showMessage(getString(R.string.other_exception) + throwable.message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun logout() {
        // Write logout code
        if (session.isInitial)
            session.clearSession()
        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
    }

    /**
     * This method is used for binding view with your binding
     */
    protected abstract fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ): T

    protected abstract fun bindData()

    protected abstract fun setUpToolbar()

    private fun showErrorSnackBar(message: String) {
        hideKeyBoard()
        if (view != null) {
            val snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            snackbar.duration = 3000

            snackbar.setActionTextColor(Color.WHITE)
            snackbar.setAction("OK") { snackbar.dismiss() }
            val snackView = snackbar.view
            val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            val params = snackView.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackView.layoutParams = params

            snackView.setBackgroundColor(requireActivity().resources.getColor(R.color.colorRed))
            snackbar.show()
        }
    }

    fun showSomethingMessage() {
        showErrorSnackBar(getString(R.string.other_exception))
    }

    fun goBack () {
        (activity as BaseActivity).goBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getUserCurrentLocation(onLocation: (LatLng) -> Unit, update: Boolean = false) {
        locationProvider = LocationProvider((activity as BaseActivity),this)
        locationProvider?.getCurrentLocation(updated = update) {
            it?.let {
                val lat = it.latitude.toString()
                val long = it.longitude.toString()
                val location =
                    LatLng(
                        lat.toDouble(), long.toDouble()
                    )
                onLocation(location)
            }
        }
    }

    fun getAddressLocation(center: LatLng?) : Triple<String,String,String>? {
        var lat: String = ""
        var long: String = ""
        var address: String = ""
        center?.let {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
             lat = center.latitude.toString()
             long = center.longitude.toString()
            if (!addresses.isNullOrEmpty()) {
                address = addresses[0].getAddressLine(0)
                return Triple(lat,long,address)
            }
        }
        return null
    }
}

