package com.helloyatri.ui.home.adapter

import android.view.ViewGroup
import com.helloyatri.data.model.Review
import com.helloyatri.databinding.AccountAllReviewsRowItemBinding
import com.helloyatri.ui.base.adavancedrecyclerview.AdvanceRecycleViewAdapter
import com.helloyatri.ui.base.adavancedrecyclerview.BaseHolder
import com.helloyatri.utils.AppUtils.doubleDefault
import com.helloyatri.utils.extension.loadPassengerImageFromServerWithPlaceHolder
import com.helloyatri.utils.extension.nullify
import com.helloyatri.utils.extension.toBinding

class AdapterAccountAllReviews :
        AdvanceRecycleViewAdapter<AdapterAccountAllReviews.ViewHolder, Review>() {

    inner class ViewHolder(private val binding: AccountAllReviewsRowItemBinding) :
            BaseHolder<Review>(binding.root) {

        override fun bind(item: Review) = with(binding) {
            imageViewUserProfile.loadPassengerImageFromServerWithPlaceHolder(item.reviewer?.profileImage)
            textViewUserName.text = item.reviewer?.name?.nullify() ?: ""
            textViewDateAndTime.text = item.createdAt.nullify()
            textViewRatings.text = item.rating?.doubleDefault("0.0")
            textViewReviewDetails.text = item.comment.nullify()
        }
    }

    override fun createDataHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindDataHolder(holder: ViewHolder, position: Int, item: Review) {
        holder.bind(item)
    }
}
