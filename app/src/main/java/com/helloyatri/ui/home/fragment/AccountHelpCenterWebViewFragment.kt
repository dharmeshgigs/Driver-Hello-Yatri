package com.helloyatri.ui.home.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import com.helloyatri.data.model.TabType
import com.helloyatri.databinding.WebviewFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AccountHelpCenterWebViewFragment : BaseFragment<WebviewFragmentBinding>() {

    private val url by lazy {
        arguments?.getString(URL)
    }

    private val tabType by lazy {
        arguments?.getParcelable<TabType>(TAB_TYPE)
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): WebviewFragmentBinding {
        return WebviewFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setUpWebView()
        when (tabType?.name) {
            TabType.FAQ.name -> {
                binding.webView.loadUrl("https://policies.google.com/faq")
            }

            TabType.CONTACT_US.name -> {
                binding.webView.loadUrl("https://about.google/intl/en_in/contact-google/")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() = with(binding) {
        webView.apply {
            webViewClient = MyWebView()
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
        }
    }

    class MyWebView : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?,
                                              request: WebResourceRequest?): Boolean {
            return false
        }
    }

    companion object {
        private const val URL = "url"
        const val TAB_TYPE = "TAB_TYPE"

        fun createBundle(tabType: String) = bundleOf(TAB_TYPE to tabType)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}