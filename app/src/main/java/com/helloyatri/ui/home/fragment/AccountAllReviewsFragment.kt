package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.helloyatri.R
import com.helloyatri.data.model.Review
import com.helloyatri.data.model.ReviewsResponse
import com.helloyatri.databinding.AccountAllReviewsFragmentBinding
import com.helloyatri.network.ApiViewModel
import com.helloyatri.network.Status
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.adapter.AdapterAccountAllReviews
import com.helloyatri.utils.AppUtils.doubleDefault
import com.helloyatri.utils.extension.hide
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAllReviewsFragment : BaseFragment<AccountAllReviewsFragmentBinding>() {
    private val apiViewModel by activityViewModels<ApiViewModel>()

    private val accountAllReviewsAdapter by lazy {
        AdapterAccountAllReviews()
    }

    private val accountAllReviewsDataList = ArrayList<Review>()

    override fun createViewBinding(
        inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean
    ): AccountAllReviewsFragmentBinding {
        return AccountAllReviewsFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        initObservers()
        setUpRecyclerView()
        accountAllReviewsDataList.clear()
        apiViewModel.getAllReviewAPI()
    }

    private fun initObservers() {
        apiViewModel.getAllReviewLiveData.observe(this) { resource ->
            resource?.let {
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideLoader()
                        accountAllReviewsDataList.clear()
                        it.data?.let { it ->
                            val response = Gson().fromJson(it, ReviewsResponse::class.java)
                            response?.data?.data?.takeIf { it.isNotEmpty() }?.let {
                                accountAllReviewsDataList.addAll(it)
                                setUpData()
                            }
                            response?.data?.DETAILS?.let {
                                binding.textViewOverallAverage.text =
                                    it.TITLE.nullify(getString(R.string.label_overall_average))
                                binding.textViewRatings.text =
                                    it.averageReviewRating.doubleDefault("0.0")
                            }
                        }
                        showPlaceholder()
                    }

                    Status.ERROR -> {
                        hideLoader()
                        showPlaceholder()
                    }

                    Status.LOADING -> showLoader()
                }
            } ?: run {
                hideLoader()
                showPlaceholder()
            }
        }
    }

    private fun showPlaceholder() = with(binding) {
        if (accountAllReviewsDataList.isEmpty()) {
            textViewPlaceholder.show()
        } else {
            textViewPlaceholder.hide()
        }
    }

    private fun setUpRecyclerView() = with(binding) {
        recyclerViewReviews.apply {
            adapter = accountAllReviewsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpData() {
        accountAllReviewsAdapter.setItems(accountAllReviewsDataList, 1)
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(true).showBackButton(true)
            .setToolbarTitle(getString(R.string.title_all_reviews)).build()
    }
}