package com.helloyatri.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.gms.maps.SupportMapFragment
import com.helloyatri.core.Session
import com.helloyatri.ui.activity.AuthActivity
import com.helloyatri.ui.base.loader.Loader
import com.helloyatri.ui.manager.Navigator
import com.helloyatri.utils.Validator
import javax.inject.Inject

abstract class MapBaseFragment<T : ViewBinding> : SupportMapFragment() {

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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


    fun <T : MapBaseFragment<*>> getParentFragment(targetFragment: Class<T>): T? {
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




    open fun onBackActionPerform(): Boolean {
        return true
    }

    open fun onViewClick(view: View) {

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

}

