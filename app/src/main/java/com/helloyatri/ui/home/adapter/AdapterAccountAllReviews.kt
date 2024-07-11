package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.response.AccountAllReviews
import com.helloyatri.databinding.AccountAllReviewsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.extension.loadImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.toBinding

class AdapterAccountAllReviews :
        AdvanceRecycleViewAdapter<AdapterAccountAllReviews.ViewHolder, AccountAllReviews>() {

    inner class ViewHolder(private val binding: AccountAllReviewsRowItemBinding) :
            BaseHolder<AccountAllReviews>(binding.root) {

        override fun bind(item: AccountAllReviews) = with(binding) {
            imageViewUserProfile.loadImageFromServerWithPlaceHolder(item.userImage)
            textViewUserName.text = item.userName
            textViewDateAndTime.text = item.date
            textViewRatings.text = item.userRating
            textViewReviewDetails.text = item.userReview

            root.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: AccountAllReviews) {
        holder.bind(item)
    }
}
