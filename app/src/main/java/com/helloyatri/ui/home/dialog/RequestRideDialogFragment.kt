package com.helloyatri.ui.home.dialog

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.helloyatri.R
import com.helloyatri.data.model.TripRiderModel
import com.helloyatri.databinding.RequestRideDialogFragmentBinding
import com.helloyatri.ui.base.BaseDialogFragment
import com.helloyatri.utils.extension.trimmedText
import com.helloyatri.utils.textdecorator.TextDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestRideDialogFragment(
    private val acceptCallBack: () -> Unit,
    private val declineCallBack: () -> Unit,
    tripRiderModel: TripRiderModel
) :
        BaseDialogFragment<RequestRideDialogFragmentBinding>() {

    private lateinit var countdownTimer: CountDownTimer

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?,
                                   attachToRoot: Boolean): RequestRideDialogFragmentBinding {
        return RequestRideDialogFragmentBinding.inflate(layoutInflater)
    }

    override fun bindData() {
        isCancelable = false
        setUpTimer()
        setClickListener()
        setTextDecorator()
    }

    private fun setTextDecorator() = with(binding) {
        TextDecorator.decorate(textViewUserName, textViewUserName.trimmedText)
                .setTypeface(R.font.lufga_medium, getString(R.string.dummy_username_value))
                .setAbsoluteSize(resources.getDimensionPixelSize(R.dimen._14ssp),
                        getString(R.string.dummy_username_value)).build()

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

    private fun setClickListener() = with(binding) {
        textViewAccept.setOnClickListener {
            countdownTimer.cancel()
            acceptCallBack.invoke()
            dismiss()
        }

        textViewDecline.setOnClickListener {
            countdownTimer.cancel()
            declineCallBack.invoke()
            dismiss()
        }
    }

    private fun setUpTimer() = with(binding) {
        roundSeekBar.maxProgress = 60
        countdownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 == 45L) {
                    roundSeekBar.setPrimaryColor(
                            ContextCompat.getColor(requireContext(), R.color.seekbarYellowColor),
                            false)
                } else if (millisUntilFinished / 1000 == 30L) {
                    roundSeekBar.setPrimaryColor(
                            ContextCompat.getColor(requireContext(), R.color.seekbarPrimaryColor),
                            false)
                } else if (millisUntilFinished / 1000 == 15L) {
                    roundSeekBar.setPrimaryColor(
                            ContextCompat.getColor(requireContext(), R.color.seekbarRedColor),
                            false)
                }
                roundSeekBar.progress = millisUntilFinished / 1000
                roundSeekBar.secondaryProgress = millisUntilFinished / 1000
                textViewTimer.text = updateTimerUI(millisUntilFinished)
                roundSeekBar.animate()
            }

            override fun onFinish() {
                dismiss()
            }
        }.start()
    }

    private fun updateTimerUI(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

}