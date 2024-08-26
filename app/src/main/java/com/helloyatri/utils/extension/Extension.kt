package com.helloyatri.utils.extension

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.onDenied
import com.fondesa.kpermissions.extension.onPermanentlyDenied
import com.fondesa.kpermissions.extension.onShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.helloyatri.R
import com.helloyatri.utils.Formatter.DD_MMM_YYYY
import org.apache.commons.lang3.StringEscapeUtils
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.ENGLISH)
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.NARROW, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

fun Int.toMonthName(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, this - 1) // Subtract 1 because Calendar months are 0-based

    val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun String.toMonthNumber(): Int {
    val dateFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val date = dateFormat.parse(this)

    val calendar = Calendar.getInstance()
    calendar.time = date

    return calendar.get(Calendar.MONTH) + 1 // Adding 1 to get the month number
}

fun View.isVisible(isVisible: Boolean) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.isInVisible(isInVisible: Boolean) {
    if (isInVisible) {
        this.visibility = View.INVISIBLE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun View.isInVisible() = this.visibility == View.INVISIBLE

fun View.setDrawable(drawable: Int?) {
    drawable?.let {
        this.background = ResourcesCompat.getDrawable(
            resources, drawable, null
        )
    } ?: run {
        this.background = null
    }
}

fun AppCompatEditText.clear() = this.setText("")

fun AppCompatTextView.setGradientTextColor(startColor: Int, endColor: Int) {
    paint.shader = LinearGradient(
        55f, 0f, 0f, textSize, context.getColor(startColor), context.getColor(
            endColor
        ), Shader.TileMode.CLAMP
    )
}

fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = color

    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
}


fun Fragment.setAsFullScreen(shouldApplyInsetBottom: Boolean = true) {
    val window = requireActivity().window
    window.statusBarColor = Color.TRANSPARENT
    WindowCompat.setDecorFitsSystemWindows(window, false)
    view?.let {
        it.setOnApplyWindowInsetsListener { v, insets ->
            if (shouldApplyInsetBottom)
                v.updatePadding(bottom = insets.stableInsetBottom)
            insets
        }

        // WindowInsetsControllerCompat(window, it).isAppearanceLightStatusBars = true
    }
}

fun AppCompatEditText.setTextConstraint(
    allowAlphanumeric: Boolean = false,
    allowSpace: Boolean = true
) {
    val regex = when {
        allowAlphanumeric && allowSpace -> {
            "[a-zA-Z0-9 ]*"
        }

        allowAlphanumeric && !allowSpace -> {
            "[a-zA-Z0-9]*"
        }

        !allowAlphanumeric && allowSpace -> {
            "[a-zA-Z ]*"
        }

        else -> {
            "[a-zA-Z]*"
        }
    }

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            if (!p0.toString().matches(Regex(regex))) {
                text?.let {
                    setText(it.dropLast(1).toString())
                }
                text?.length?.let { setSelection(it) }
            }
        }
    })
}

val TextView.trimmedText get() = text.toString().trim()
val TextView.textWithoutTrim get() = text.toString()

fun Number?.numberFormatter(
    prefix: String = "", format: String = "#0.00", formatWithoutSuffix: String = "#,##0.00"
): String {
    val suffix = charArrayOf(' ')
    val numValue = this?.toLong() ?: 0
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        (DecimalFormat(if (prefix == "") format else "$prefix $format").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]).also { return it }
    } else {
        DecimalFormat(if (prefix == "") formatWithoutSuffix else "$prefix$formatWithoutSuffix").format(
            this ?: 0.00
        )
    }
}

fun Number?.currencyNumberFormatter(
    format: String = "#0.00",
    formatWithoutSuffix: String = "#,##0.00",
): String {
    return this.numberFormatter(
        prefix = "R",
        format = format,
        formatWithoutSuffix = formatWithoutSuffix
    )
}

/**
 * This method is used to format number.
 * Example: $ 5.0k
 * */
fun AppCompatTextView.numberFormatter(
    number: Number?,
    prefix: String = "",
    format: String = "#0.00",
    formatWithoutSuffix: String = "#,##0.00"
) {
    text = number.numberFormatter(prefix, format, formatWithoutSuffix)
}

fun AppCompatTextView.numberFormatterWithoutDecimal(number: Number?, prefix: String = "") {
    text = number.numberFormatter(format = "#0", formatWithoutSuffix = "#,##0", prefix = prefix)
}

fun Float.numberFormatterWithoutDecimal(prefix: String = ""): String {
    return this.numberFormatter(format = "#0", formatWithoutSuffix = "#,##0", prefix = prefix)
}

fun ArrayList<String>.commaSeparatedString(): String {
    return this.joinToString().replace(" ", "")
}

val Int.booleanValue get() = this != 0

/**
 * This method is used to set icon. Do not pass any arguments if you want to remove icon.
 * */
fun TextView.setIcon(
    @DrawableRes startIcon: Int = 0,
    @DrawableRes topIcon: Int = 0,
    @DrawableRes endIcon: Int = 0,
    @DrawableRes bottomIcon: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(
        startIcon, topIcon, endIcon, bottomIcon
    )
}

fun TextView.removeIcon() {
    setIcon()
}

fun TextView.setColorToText(@ColorRes color: Int) {
    setTextColor(
        ResourcesCompat.getColor(
            resources, color, null
        )
    )
}

/*fun Resources.dpToPx(dp: Int): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics
        )
    )
}*/

fun Resources.dpToPx(dp: Int): Int {
    return (displayMetrics.density * getDimension(dp)).toInt()
}

inline fun <reified V : ViewBinding> ViewGroup.toBinding(): V {
    return V::class.java.getMethod(
        "inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java
    ).invoke(null, LayoutInflater.from(context), this, false) as V
}

fun MaterialTimePicker.setOnPositiveButtonClickListener(onPositiveButtonClick: (selectedTime: String) -> Unit) {
    addOnPositiveButtonClickListener {
        val selectedTime = StringBuilder()
        selectedTime.append(
            when {
                hour > 12 -> {
                    if (minute < 10) {
                        "${hour - 12}:0${minute} PM"
                    } else {
                        "${hour - 12}:${minute} PM"
                    }
                }

                hour == 12 -> {
                    if (minute < 10) {
                        "${hour}:0${minute} PM"
                    } else {
                        "${hour}:${minute} PM"
                    }
                }

                hour == 0 -> {
                    if (minute < 10) {
                        "${hour + 12}:0${minute} AM"
                    } else {
                        "${hour + 12}:${minute} AM"
                    }
                }

                else -> {
                    if (minute < 10) {
                        "${hour}:0${minute} AM"
                    } else {
                        "${hour}:${minute} AM"
                    }
                }
            }
        )

        if (hour > 12 && hour - 12 < 10) {
            selectedTime.insert(0, '0')
        } else if (hour in 1..9) {
            selectedTime.insert(0, '0')
        }

        // It will call this lambda function after formatting the time.
        onPositiveButtonClick(selectedTime.toString())
    }
}

fun TextView.setColorOnText(@ColorRes color: Int) {
    setTextColor(
        ResourcesCompat.getColor(
            resources, color, null
        )
    )
}

fun ImageView?.loadImageFromServerWithPlaceHolder(path: String?) {
    this?.context?.let {
        if (!path.isNullOrEmpty()) {
            Glide.with(it).load(path).into(this)
        } else {
            Glide.with(it).load(R.drawable.icon_placeholder)
        }
    }
}

fun isValidContextForGlide(context: Context?): Boolean {
    if (context == null) {
        return false
    }
    if (context is Activity) {
        val activity = context as Activity?
        if (activity!!.isDestroyed || activity.isFinishing) {
            return false
        }
    }
    return true
}

fun AppCompatImageView.load(strImage: String, isCenterCrop: Boolean = true) {
    if (isValidContextForGlide(this.context)) {
        if (isCenterCrop) {
            Glide.with(this.context)
                .asDrawable()
                .load(strImage)
                .apply(RequestOptions().centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .into(this)
        } else {
            Glide.with(this.context)
                .asDrawable()
                .load(strImage)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .into(this)
        }
    }
}

fun AppCompatEditText.setEmojiText(str: String) {
    this.setText(StringEscapeUtils.unescapeJava(str))
}

fun Fragment.removeFromFullScreen() {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, true)
    window.statusBarColor = Color.WHITE
    view?.let {
        it.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = 0, top = insets.systemWindowInsetTop)
            insets
        }

        WindowInsetsControllerCompat(window, it).let { controller ->
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true
        }
    }
}

fun getDatePickerDialogFrom(
    context: Context,
    timeInMillis: Long? = null,
    isForTo: Boolean = false,
    selectedDate: String? = null,
    dateFormat: String? = null,
    onDateSelected: (formattedDate: String, timeInMillis: Long?) -> Unit
): MaterialDatePicker<Long> {

    val calendarConstraintBuilder = CalendarConstraints.Builder()
    val materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker()
    materialDatePickerBuilder.setTitleText(context.getString(R.string.label_select_date))

    if (isForTo) {
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        val selectedCalendar = Calendar.getInstance()

        try {
            val date = selectedDate?.let { sdf.parse(it) }
            date?.let {
                selectedCalendar.time = it
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        materialDatePickerBuilder.setCalendarConstraints(
            calendarConstraintBuilder.setValidator(
                DateValidatorPointForward.from(selectedCalendar.timeInMillis)
            ).build()
        )
    } else {
        materialDatePickerBuilder.setCalendarConstraints(
            calendarConstraintBuilder.setValidator(
                DateValidatorPointBackward.now()
            ).build()
        )
    }

    timeInMillis?.let {
        materialDatePickerBuilder.setSelection(it)
    } ?: run {
        materialDatePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
    }

    val materialDatePicker = materialDatePickerBuilder.build()
    materialDatePicker.addOnPositiveButtonClickListener {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = it
        val format = SimpleDateFormat(DD_MMM_YYYY, Locale.ENGLISH)
        val formatted: String = format.format(utc.time)
        onDateSelected(formatted, it)
    }
    return materialDatePicker
}

fun Fragment.requirePermission(permissionName: String, callback: (Boolean) -> Unit) {
    val request = requireActivity().permissionsBuilder(permissionName).build()
    request.onAccepted {
        callback(true)
    }.onDenied {

    }.onPermanentlyDenied {
        Log.d("Hello Yatri", "Permanent Denied")
        callback(false)

    }.onShouldShowRationale { _, nonce ->
        nonce.use()
    }
    request.send()
}

fun Button.enableButton(isEnable: Boolean) {
    backgroundTintList = ContextCompat.getColorStateList(
        this.context, if (isEnable)
            R.color.colorPrimary
        else
            R.color.grey

    )
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun TextView.enableTextView(isEnable: Boolean) {
    backgroundTintList = ContextCompat.getColorStateList(
        this.context, if (isEnable)
            R.color.colorPrimary
        else
            R.color.grey

    )
}

fun String?.nullify() = this ?: ""

fun String?.nullify(default: String) = this ?: default
