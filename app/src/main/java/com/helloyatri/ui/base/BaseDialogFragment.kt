package com.helloyatri.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.helloyatri.R
import com.helloyatri.core.Session
import com.helloyatri.exception.ApplicationException
import com.helloyatri.exception.AuthenticationException
import com.helloyatri.exception.ServerException
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.manager.Navigator
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

abstract class BaseDialogFragment<T : ViewBinding> : DialogFragment() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var session: Session

    private var _binding: T? = null

    protected val binding: T
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = createViewBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        with(dialog?.window) {
            this?.setGravity(Gravity.CENTER or Gravity.CENTER)
        }

        bindData()
    }

    open fun onShow() {
    }

    fun showMessage(message: String) {
        showSnackBar(message)
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
            snackbar.setAction("OK", View.OnClickListener { snackbar.dismiss() })
            val snackView = snackbar.view
            val textView: TextView = snackView.findViewById(
                                com.google.android.material.R.id.snackbar_text)
            textView.maxLines = 4

            snackView.setBackgroundColor(
                    requireActivity().resources.getColor(R.color.colorPrimary))
            snackbar.show()
        }
    }

    private fun showSnackBar(message: String, viewSet: View) {
        hideKeyBoard()
        val snackbar = Snackbar.make(viewSet, message, Snackbar.LENGTH_LONG)
        snackbar.duration = 3000
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.setAction("OK", View.OnClickListener { snackbar.dismiss() })
        val snackView = snackbar.view
        val textView: TextView =
                snackView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 4
        snackView.setBackgroundColor(
                requireActivity().resources.getColor(R.color.colorPrimary))
        snackbar.show()
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

    fun onError(throwable: Throwable) {
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

    fun logout() {
        session.isInitial = false
        session.user = null
        session.isLoggedIn = false
        session.clearSession()
        navigator.loadActivity(AuthActivity::class.java).byFinishingAll().start()
    }

    protected abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                             attachToRoot: Boolean): T

    protected abstract fun bindData()
}