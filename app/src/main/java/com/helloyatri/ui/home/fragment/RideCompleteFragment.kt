package com.helloyatri.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.helloyatri.R
import com.helloyatri.databinding.RideCompleteFragmentBinding
import com.helloyatri.ui.base.BaseFragment
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.CANCEL_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.ICON
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.OK_TEXT
import com.helloyatri.ui.home.dialog.CommonYesNoDialogFragment.Companion.TITLE
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideCompleteFragment : BaseFragment<RideCompleteFragmentBinding>() {
    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): RideCompleteFragmentBinding {
        return RideCompleteFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        setTextDecorator()
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        textViewCollected.setOnClickListener {
            val commonYesNoDialogFragment = CommonYesNoDialogFragment {
                if (it == CommonYesNoDialogFragment.YES) {
                    activity?.apply {
                        finish()
                    }
                }
            }

            commonYesNoDialogFragment.arguments = bundleOf(
                    TITLE to getString(R.string.label_have_you_collected_money_from_to_pooja),
                    CANCEL_TEXT to getString(R.string.btn_no_still_not),
                    OK_TEXT to getString(R.string.label_yes_collected),
                    ICON to R.drawable.image_money_collect)

            commonYesNoDialogFragment.show(childFragmentManager,
                    PickUpSpotFragment::class.java.simpleName)
        }
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewNote, textViewNote.trimmedText)
                .setTextColor(R.color.homeBgBlueColor, "Note:").build()

        TextDecorator.decorate(textViewFairPrice, textViewFairPrice.trimmedText)
                .setTypeface(R.font.lufga_medium, "₹780")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "₹780").build()

        TextDecorator.decorate(textViewDistance, textViewDistance.trimmedText)
                .setTypeface(R.font.lufga_medium, "25.5 Km")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "25.5 Km").build()

        TextDecorator.decorate(textViewDuration, textViewDuration.trimmedText)
                .setTypeface(R.font.lufga_medium, "45 min")
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp), "45 min").build()
    }

    override fun setUpToolbar() = with(toolbar) {
        showToolbar(false).build()
    }
}