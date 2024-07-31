package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.helloyatri.R
import com.helloyatri.data.model.AccountAllReviews
import com.helloyatri.databinding.AccountAllReviewsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountAllReviews
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAllReviewsFragment : BaseFragment<AccountAllReviewsFragmentBinding>() {
    private val apiViewModel by viewModels<ApiViewModel>()

    private val accountAllReviewsAdapter by lazy {
        AdapterAccountAllReviews()
    }

    private val accountAllReviewsDataList = ArrayList<AccountAllReviews>()

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): AccountAllReviewsFragmentBinding {
        return AccountAllReviewsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        apiViewModel.getAllReviewAPI()
        setUpRecyclerView()
        initObservers()

    }

    private fun initObservers() {
        apiViewModel.getAllReviewLiveData.observe(this){resource ->
            when(resource.status){
                Status.SUCCESS -> {
                    hideLoader()
                }
                Status.ERROR -> {
                    hideLoader()
                    setUpData()
                }
                Status.LOADING -> showLoader()
            }

        }
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewReviews.apply {
            adapter = accountAllReviewsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpData() {
        accountAllReviewsDataList.clear()
        for (item in 1..5) {
            accountAllReviewsDataList.add(AccountAllReviews(
                    userImage = "https://images.unsplash.com/photo-1602233158242-3ba0ac4d2167?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8R2lybHxlbnwwfHwwfHx8MA%3D%3D",
                    userName = "Pooja Patel", date = "Today, 11:45 AM", userRating = "4.0",
                    userReview = "Excellent. Good driver and car too, overall I reach my destination on time."))
        }
        accountAllReviewsAdapter.setItems(accountAllReviewsDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
                .setToolbarTitle(getString(R.string.title_all_reviews)).build()
    }
}