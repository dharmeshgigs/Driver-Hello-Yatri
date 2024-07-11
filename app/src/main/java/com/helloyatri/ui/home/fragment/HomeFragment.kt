package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import com.helloyatri.R
import com.helloyatri.databinding.HomeFragmentBinding
import com.helloyatri.ui.activity.IsolatedActivity
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.HomeActivity
import com.helloyatri.ui.home.adapter.AdapterRidesForPickups
import com.helloyatri.ui.home.dialog.RequestRideDialogFragment
import com.helloyatri.utils.extension.changeStatusBarColor
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.show
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.getRidePickUpList
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>() {

    private var isOnline = false

    private val adapterPickUp by lazy {
        AdapterRidesForPickups()
    }

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        requireActivity().changeStatusBarColor(
                ContextCompat.getColor(requireContext(), R.color.backgroundColor), true)
        setTextDecorator()
        setUpUi()
        setClickListener()
        setAdapter()
    }

    private fun showRequestDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            RequestRideDialogFragment {
                navigator.loadActivity(IsolatedActivity::class.java, PickUpSpotFragment::class.java)
                        .start()
            }.show(childFragmentManager, HomeFragment::class.java.simpleName)
        }
    }

    private fun setAdapter() = with(binding) {
        recyclerViewPickUps.apply {
            adapter = adapterPickUp
            adapterPickUp.setItems(requireActivity().getRidePickUpList(), 1)
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }
    }

    private fun setClickListener() = with(binding) {
        imageViewUserProfile.setOnClickListener {
            (activity as HomeActivity).openDrawer()
        }

        textViewOnlineOfflineStatus.setOnClickListener {
            isOnline = !isOnline
            changeStatus()
        }

        textViewLabelViewAll.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, ScheduleRideFragment::class.java)
                    .start()
        }

        imageViewNotification.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, NotificationFragment::class.java)
                    .start()
        }

        textViewRideRequestOnlineOfflineStatus.setOnClickListener {
            isOnline = !isOnline
            changeStatus()
            textViewOnlineOfflineStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.grey)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_offline)
            textViewRideRequest.hide()
            constraintLayoutRideRequest.hide()
        }

        adapterPickUp.setOnViewItemClickListener { _, view ->
            when (view.id) {
                R.id.textViewNavigateTo -> {
                    navigator.loadActivity(IsolatedActivity::class.java,
                            StartRideFromFragment::class.java).start()
                }
            }
        }

        textViewIntercityRides.setOnClickListener {
            navigator.loadActivity(IsolatedActivity::class.java, IntercityRideFragment::class.java)
                    .start()
        }
    }

    private fun changeStatus() = with(binding) {
        if (isOnline) {
            showRequestDialog()
            textViewOnlineOfflineStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.grey)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_offline)
            textViewRideRequest.hide()
            constraintLayoutRideRequest.hide()
        } else {
            textViewOnlineOfflineStatus.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.homeBgBlueColor)
            textViewOnlineOfflineStatus.text = getString(R.string.label_go_online)
            textViewRideRequest.show()
            constraintLayoutRideRequest.show()
        }
    }

    private fun setUpUi() = with(binding) {
        imageViewUserProfile.loadImageFromServerWithPlaceHolder(
                "https://plus.unsplash.com/premium_photo-1669047668540-9e1712e29f1f?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewUserName, textViewUserName.trimmedText)
                .setTypeface(R.font.lufga_medium, getString(R.string.dummy_username_value))
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp),
                        getString(R.string.dummy_username_value)).build()

        TextDecorator.decorate(textViewRide, textViewRide.trimmedText)
                .setTypeface(R.font.lufga_medium, "3")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "3").build()

        TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                .setTypeface(R.font.lufga_medium, "105.5 Km")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "105.5 Km")
                .build()

        TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                .setTypeface(R.font.lufga_medium, "02:40 Hr")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "02:40 Hr")
                .build()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}