package com.helloyatri.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.databinding.HomeAcitivtyBinding
import com.helloyatri.ui.base.BaseActivity
import com.helloyatri.ui.home.dialog.LogoutDialogFragment
import com.helloyatri.ui.home.fragment.AccountAllReviewsFragment
import com.helloyatri.ui.home.fragment.AccountDocumentsFragment
import com.helloyatri.ui.home.fragment.AccountEditProfileFragment
import com.helloyatri.ui.home.fragment.AccountHelpCenterFragment
import com.helloyatri.ui.home.fragment.AccountPaymentFragment
import com.helloyatri.ui.home.fragment.AccountPreferencesFragment
import com.helloyatri.ui.home.fragment.AccountSavedAddressFragment
import com.helloyatri.ui.home.fragment.HomeFragment
import com.helloyatri.ui.home.fragment.RideActivityFragment
import com.helloyatri.ui.home.sidemenu.SideMenu
import com.helloyatri.ui.home.sidemenu.SideMenuAdapter
import com.helloyatri.ui.home.sidemenu.SideMenuTag
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    private lateinit var homeAcitivtyBinding: HomeAcitivtyBinding

    private val sideMenuAdapter by lazy {
        SideMenuAdapter()
    }

    private val sideMenuList = ArrayList<SideMenu>()

    override fun findFragmentPlaceHolder(): Int {
        return R.id.placeHolder
    }

    override fun createViewBinding(): View {
        homeAcitivtyBinding = HomeAcitivtyBinding.inflate(layoutInflater)
        return homeAcitivtyBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setUpSideMenu()
        setUpSideMenuClickListener()
        load(HomeFragment::class.java).replace(false)
        homeAcitivtyBinding.navigationDrawerContent.imageViewUserProfile.loadImageFromServerWithPlaceHolder(
                "https://plus.unsplash.com/premium_photo-1669047668540-9e1712e29f1f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    }

    fun openDrawer() {
        hideKeyboard()
        homeAcitivtyBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        homeAcitivtyBinding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun setUpSideMenu() = with(homeAcitivtyBinding) {
        navigationDrawerContent.recyclerViewSideMenu.apply {
            adapter = sideMenuAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }
        sideMenuList.clear()
        sideMenuList.add(
                SideMenu(sideMenuName = "Edit Profile", sideMenuTag = SideMenuTag.EDIT_PROFILE))
        sideMenuList.add(
                SideMenu(sideMenuName = "Ride Activity", sideMenuTag = SideMenuTag.RIDE_ACTIVITY))
        sideMenuList.add(SideMenu(sideMenuName = "Payment", sideMenuTag = SideMenuTag.PAYMENT))
        sideMenuList.add(
                SideMenu(sideMenuName = "Saved Address", sideMenuTag = SideMenuTag.SAVED_ADDRESS))
        sideMenuList.add(
                SideMenu(sideMenuName = "Preferences", sideMenuTag = SideMenuTag.PREFERENCES))
        sideMenuList.add(
                SideMenu(sideMenuName = "Help Center", sideMenuTag = SideMenuTag.HELP_CENTER))
        sideMenuList.add(SideMenu(sideMenuName = "Documents", sideMenuTag = SideMenuTag.DOCUMENTS))
        sideMenuList.add(SideMenu(sideMenuName = "Logout", sideMenuTag = SideMenuTag.LOGOUT))
        sideMenuAdapter.setItems(sideMenuList, 1)
    }

    private fun setUpSideMenuClickListener() = with(homeAcitivtyBinding) {
        navigationDrawerContent.imageViewCancel.setOnClickListener {
            closeDrawer()
        }

        navigationDrawerContent.textViewRatings.setOnClickListener {
            load(AccountAllReviewsFragment::class.java).replace(true)
            closeDrawer()
        }

        sideMenuAdapter.setOnItemClickListener {
            when (it.sideMenuTag) {
                SideMenuTag.EDIT_PROFILE -> {
                    load(AccountEditProfileFragment::class.java).replace(true)
                }

                SideMenuTag.RIDE_ACTIVITY -> {
                    load(RideActivityFragment::class.java).replace(true)
                }

                SideMenuTag.PAYMENT -> {
                    load(AccountPaymentFragment::class.java).replace(true)
                }

                SideMenuTag.SAVED_ADDRESS -> {
                    load(AccountSavedAddressFragment::class.java).replace(true)
                }

                SideMenuTag.PREFERENCES -> {
                    load(AccountPreferencesFragment::class.java).replace(true)
                }

                SideMenuTag.HELP_CENTER -> {
                    load(AccountHelpCenterFragment::class.java).replace(true)
                }

                SideMenuTag.DOCUMENTS -> {
                    load(AccountDocumentsFragment::class.java).replace(true)
                }

                SideMenuTag.LOGOUT -> {
                    val logoutDialogFragment = LogoutDialogFragment()
                    logoutDialogFragment.show(supportFragmentManager, logoutDialogFragment.tag)
                }
            }
            closeDrawer()
        }
    }

    private fun setUpToolbar() = with(homeAcitivtyBinding) {
        setToolbar(customToolbar)
        showToolbar(false)
    }

}