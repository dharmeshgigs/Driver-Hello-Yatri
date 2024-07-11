package com.helloyatri.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.StringDef
import com.helloyatri.R
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A class to format datetime strings
 */
object Formatter {
    internal var suffixes = arrayOf(
        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
        //    10    11    12    13    14    15    16    17    18    19
        "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
        //    20    21    22    23    24    25    26    27    28    29
        "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
        //    30    31
        "th", "st"
    )//    0     1     2     3     4     5     6     7     8     9

    // CREATE DateFormatSymbols WITH ALL SYMBOLS FROM (DEFAULT) Locale
    private val symbols: DateFormatSymbols = DateFormatSymbols(Locale.US)

    val EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY = "EEE MMM dd HH:mm:ss zzz yyyy"


    const val JAN_2017 = "MMM / yyyy"
    const val MM_YY = "MM/yy"
    const val MM_YYYY = "MM/yyyy"
    const val MMM_YYYY = "MMM yyyy"
    const val MMM_S_YYYY = "MMM, yyyy"
    const val MMM = "MMM"
    const val MM = "MM"
    const val MMMM = "MMMM"
    const val YYYY = "yyyy"
    const val MMMM_YYYY = "MMMM yyyy"
    const val DD_MM_YYYY = "dd/MM/yyyy"
    const val MM_DD_YYYY = "MM/dd/yyyy"
    const val DD_MMM_YYYY = "dd MMM yyyy"
    const val DD = "dd"
    const val D = "d"

    const val DD_MMMM_YYYY_FULL = "dd MMMM, yyyy"
    const val DD_MMM_YYYY_FULL = "dd MMM, yyyy"

    /**
     * Date format with date suffix
     * e.g 12th Dec, 2018
     */
    const val DD_S_MMMM_YYYY_FULL = "dd s MMMM, yyyy"

    const val DD_MMMM_YYYY = "dd MMMM yyyy"
    const val DD_MMMM_YYYY_SLASH = "dd/MM/yyyy"
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val DD_MM_YYYY_HH_MM = "dd MMM, YYYY 'at' HH:mm a"
    const val YYYY_MM_DD_hh_mm_aa = "yyyy-MM-dd hh:mm aa"
    const val DD_MMMM_YYYY_hh_mm_aa = "dd MMMM yyyy hh:mm aa"
    const val DD_MMM_YYYY_hh_mm_aa = "dd MMM yyyy hh:mm aa"
    const val CALL_LOG_TIME = "dd MMM, yyyy - hh:mm aa"
    const val YYYY_MM_DD_T_HH_MM_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val YYYY_MM_DD_T_HH_MM_SSS = "yyyy-MM-dd'T'HH:mm:ss:sss"
    const val DD_MMM_YYYY_SLASH_HH_MM_SS = "dd MMM, yyyy | hh:mm aa"
    const val YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val MMM_D_YYYY = "MMM d, yyyy"
    const val MMM_D_YYYY_WITHOUT_SEPARATION = "MMM d yyyy"
    const val NEW_POST_FORMAT = "MMM dd, yy | hh:mm aa"
    const val NEW_POST_COMMENT_FORMAT = "MMM dd, yy"
    const val NEW_POST_COMMENT_FORMAT_WITHOUT_SEPRATION = "dd MMM yy"
    const val NEW_WITHOUT_SEPRATION = "dd MMM yyyy"
    private const val UTC = "UTC"

    const val HH_mm = "HH:mm"
    const val HH_mm_ss = "HH:mm:ss"
    const val hh_mm_aa = "hh:mm aa"
    const val hh_mm_a = "hh:mm a"
    const val h_mm_a = "h_mm_a"
    const val hh_mm__ss_aa = "hh:mm:ss aa"
    const val aa = "aa"
    const val hh_mm = "hh:mm"

    private val SECOND: Long = 1000
    private val MINUTE = SECOND * 60
    private val HOUR = MINUTE * 60
    private val DAY = HOUR * 24
    private val WEEK = DAY * 7
    private val MONTH = DAY * 30
    private val YEAR = MONTH * 12

    private val inputTimeZone = UTC

    init {
        // OVERRIDE SOME symbols WHILE RETAINING OTHERS
        symbols.amPmStrings = arrayOf("am", "pm")
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(
        NEW_POST_COMMENT_FORMAT_WITHOUT_SEPRATION,
        NEW_POST_FORMAT,
        NEW_POST_COMMENT_FORMAT,
        D,
        DD,
        MM_DD_YYYY,
        YYYY_MM_DD,
        MMM_S_YYYY,
        JAN_2017,
        MM_YYYY,
        MMM_YYYY,
        hh_mm,
        aa,
        DD_MMM_YYYY,
        MM_YY,
        YYYY_MM_DD_HH_MM_SS,
        YYYY_MM_DD_T_HH_MM_SSS,
        YYYY_MM_DD_T_HH_MM_SSS_Z,
        YYYY_MM_DD_T_HH_MM_SS,
        YYYY_MM_DD_hh_mm_aa,
        CALL_LOG_TIME,
        DD_MMMM_YYYY_hh_mm_aa,
        DD_MMM_YYYY_hh_mm_aa,
        DD_MM_YYYY,
        MM,
        MMM,
        MMMM,
        YYYY,
        DD_MMMM_YYYY,
        DD_MMMM_YYYY_SLASH,
        DD_MMMM_YYYY_FULL,
        HH_mm,
        HH_mm_ss,
        hh_mm_aa,
        hh_mm_a,
        hh_mm__ss_aa,
        h_mm_a,
        MMM_D_YYYY,
        MMM_D_YYYY_WITHOUT_SEPARATION,
        NEW_WITHOUT_SEPRATION
    )
    annotation class FORMAT

    fun round(v: Double, places: Int): Double {
        var value = v
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value *= factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    fun round(v: Double): String {
        var value = v
        val factor = Math.pow(10.0, 2.0).toLong()
        value *= factor
        val tmp = Math.round(value)
        val valueV = tmp.toDouble() / factor
        return String.format(Locale.US, "%.2f", valueV)
    }

    /**
     * Check if two calendar objects has same date,month,year
     *
     * @param first
     * @param second
     * @return
     */
    fun matches(first: Calendar, second: Calendar): Boolean {
        val fDay = first.get(Calendar.DAY_OF_MONTH)
        val fMonth = first.get(Calendar.MONTH)
        val fYear = first.get(Calendar.YEAR)
        val sDay = second.get(Calendar.DAY_OF_MONTH)
        val sMonth = second.get(Calendar.MONTH)
        val sYear = second.get(Calendar.YEAR)
        return fDay == sDay && fMonth == sMonth && fYear == sYear
    }

    fun format(
        locale: Locale,
        datetime: String, @FORMAT inFormat: String, @FORMAT outFormat: String,
        isUtc: Boolean
    ): String? {
        try {

            val input = SimpleDateFormat(inFormat, locale)
            if (isUtc)
                input.timeZone = TimeZone.getTimeZone(inputTimeZone)

            val date = input.parse(datetime)

            val output = SimpleDateFormat(outFormat, locale)
            output.timeZone = TimeZone.getDefault()
            output.dateFormatSymbols = symbols

            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmOverloads
    fun format(
        datetime: String,
        @FORMAT inFormat: String,
        @FORMAT outFormat: String,
        isUtc: Boolean = false
    ): String? {
        return format(Locale.US, datetime, inFormat, outFormat, isUtc)
    }

    @JvmOverloads
    fun format(datetime: Calendar, @FORMAT outFormat: String, isUtc: Boolean = false): String? {
        try {

            val date = datetime.time
            val output = SimpleDateFormat(outFormat, Locale.US)

            if (isUtc)
                output.timeZone = TimeZone.getTimeZone(inputTimeZone)
            else
                output.timeZone = TimeZone.getDefault()

            output.dateFormatSymbols = symbols

            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun format(date: Date, @FORMAT outFormat: String): String? {
        try {
            val output = SimpleDateFormat(outFormat, Locale.US)
            output.dateFormatSymbols = symbols
            output.timeZone = TimeZone.getDefault()
            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmOverloads
    fun getMyCalendar(
        datetime: String, @FORMAT inFormat: String,
        isUtc: Boolean = false
    ): Calendar? {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currenTimeZone = Date(datetime.toLong() * 1000)
        calendar.time = currenTimeZone
        return calendar
    }

    @JvmOverloads
    fun getCalendar(datetime: String, @FORMAT inFormat: String, isUtc: Boolean = false): Calendar? {
        try {
            val output = SimpleDateFormat(inFormat, Locale.US)
            output.dateFormatSymbols = symbols
            if (isUtc)
                output.timeZone = TimeZone.getTimeZone(inputTimeZone)
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault()
            val date = output.parse(datetime)
            if (date != null)
                calendar.time = date
            return calendar
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getMessageTime(createdAtTime: Long?): String {
        val formatter = SimpleDateFormat(hh_mm_aa, Locale.US)
        return formatter.format(Date(createdAtTime!!))

    }


    fun getFormattedDate(dateString: Long): String {
        val sdf = SimpleDateFormat(DD_MMM_YYYY, Locale.US)
        return sdf.format(Date(dateString))
//        val temp = sdf.format(Date())
//        val mDate = sdf.parse(dateString)
//        val smsTimeInMilis = mDate.time
//        val smsTime = Calendar.getInstance()
//        smsTime.timeInMillis = smsTimeInMilis
//
//        val now = Calendar.getInstance()
//
//        val timeFormatString = "h:mm aa"
//        val dateTimeFormatString = "d MMM yyyy"
//        val formatter = SimpleDateFormat(dateTimeFormatString)
//
//        val HOURS = (60 * 60 * 60).toLong()
//        return if (now.get(Calendar.DATE) === smsTime.get(Calendar.DATE)) {
//            "${context.getString(R.string.label_today)} " /*+ DateFormat.format(timeFormatString, smsTime)*/
//        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) === 1) {
//            "${context.getString(R.string.label_yesterday)} " /*+ DateFormat.format(timeFormatString, smsTime)*/
//        } else if (now.get(Calendar.YEAR) === smsTime.get(Calendar.YEAR)) {
//            formatter.format(smsTime.time).toString()
//        } else {
//            formatter.format(smsTime.time).toString()
//        }
    }

    //
    fun getPrettyTime(context: Context, @Nullable datetime: String?): String {
        try {
//            val date = Formatter.getMyCalendar(datetime!!, inFormat, isUtc)!!.time

            //Initialize both calendar with set time
            val current = Calendar.getInstance()

            //DebugLog.i("InsertDate::::"+ Formatter.format(calendarDate, DD_MMMM_YYYY_hh_mm_aa))
            //DebugLog.i("CurrentDate::::"+ Formatter.format(current, DD_MMMM_YYYY_hh_mm_aa))

            val difference = current.timeInMillis - datetime!!.toLong()

            val year = difference / YEAR
            val month = difference / MONTH
            val weeks = difference / WEEK
            val day = difference / DAY
            val hour = difference / HOUR
            val minute = difference / MINUTE
            val second = difference / SECOND

            var time: String? = null
            if (year > 0)
                time = String.format(
                    "%d %s",
                    year,
                    if (year > 1) context.getString(R.string.years_ago) else context.getString(R.string.year_ago)
                )
            else if (month > 0)
                time = String.format(
                    "%d %s",
                    month,
                    if (month > 1) context.getString(R.string.months_ago) else context.getString(R.string.month_ago)
                )
            else if (weeks > 0)
                time = String.format(
                    "%d %s",
                    weeks,
                    if (weeks > 1) context.getString(R.string.weeks_ago) else context.getString(R.string.week_ago)
                )
            else if (day > 0)
                time = String.format(
                    "%d %s",
                    day,
                    if (day > 1) context.getString(R.string.days_ago) else context.getString(R.string.day_ago)
                )
            else if (hour > 0)
                time = String.format(
                    "%d %s",
                    hour,
                    if (hour > 1) context.getString(R.string.hours_ago) else context.getString(R.string.hour_ago)
                )
            else if (minute > 0)
                time = String.format(
                    "%d %s",
                    minute,
                    if (minute > 1) context.getString(R.string.minutes_ago) else context.getString(R.string.minute_ago)
                )
            else if (second > 0)
                time = String.format(
                    "%d %s",
                    second,
                    if (second > 1) context.getString(R.string.seconds_ago) else context.getString(R.string.second_ago)
                )

            return if (time != null) String.format(
                "%s",
                time
            ) else context.getString(R.string.just_now)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }


    fun getTimeorDate(datetime: String, @FORMAT inFormat: String, isUtc: Boolean): String? {
        try {
            //Initialize both calendar with set time
            val calendarDate = getCalendar(datetime, inFormat, isUtc)
            val current = Calendar.getInstance()

            return if (matches(calendarDate!!, current)) {
                format(calendarDate.time, hh_mm_aa)
            } else {
                format(calendarDate.time, CALL_LOG_TIME)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    /*fun covertTimeToText(dataDate: String): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"

        try {

            val dateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.US)

            dateFormat.timeZone = TimeZone.getTimeZone("GMT");

            val pasTime = dateFormat.parse(dataDate)

            val nowTime = Date()

            val dateDiff = nowTime.time - pasTime.time

            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)

            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                convTime = if (day > 30) {
                    (day / 30).toString() + " Months " + suffix
                } else if (day > 360) {
                    (day / 360).toString() + " Years " + suffix
                } else {
                    (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }

        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message + "")
        }

        return convTime

    }*/

    @SuppressLint("SimpleDateFormat")
    fun covertTimeToText(datetime: String): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "ago"

        try {
            val dateFormat = SimpleDateFormat(DateUtils.DateFormat.SERVER_DATE_TIME.value)
            dateFormat.timeZone = TimeZone.getDefault()

            val pasTime = dateFormat.parse(datetime)!!


            val nowTime = Date()

            val dateDiff = nowTime.time - pasTime.time

            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)

            when {
                second < 60 -> {
                    convTime = "Just now"//"$second seconds $suffix"
                }
                minute < 60 -> {
                    convTime =
                        if (minute > 1L) "$minute minutes $suffix" else "$minute minute $suffix"
                }
                hour < 24 -> {
                    convTime = if (hour > 1L) "$hour hours $suffix" else "$hour hour $suffix"
                }
                else -> {
                    Log.e("covertTimeToText: ", datetime)
                    convTime = DateUtils.format(
                        datetime as Date,
                        DateUtils.DateFormat.PRIMARY_TIME,
                        DateUtils.DateTimeZone.DEFAULT
                    )
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }

    fun resetSeconds(calendar: Calendar) {
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
    }

    fun getAge(@NonNull date: Calendar): Long {
        val age: Long = Calendar.getInstance().timeInMillis - date.timeInMillis
        return age / YEAR
    }

    fun getMaxMinutes(minutes: Int): Int {
        return if (minutes % 15 == 0) {
            0
        } else if (minutes in 1..14) {
            15 - minutes
        } else if (minutes in 16..29) {
            30 - minutes
        } else if (minutes in 31..44) {
            45 - minutes
        } else
            60 - minutes
    }

    fun getCurrentDateFormat(): String {
        val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.US)
        return sdf.format(Calendar.getInstance().time)
    }

    fun getPrettyTime(
        context: Context, locale: Locale?, datetime: String?, @FORMAT inFormat: String?,
        isUtc: Boolean
    ): String? {
        try {
            val date = getCalendar(
                datetime!!,
                inFormat!!, isUtc
            )!!.time

            //Initialize both calendar with set time
            val calendarDate = Calendar.getInstance()
            calendarDate.time = date
            val current = Calendar.getInstance()

//            DebugLog.i("InsertDate::::" + Formatter.format(calendarDate, DD_MMMM_YYYY_hh_mm_aa));
//            DebugLog.i("CurrentDate::::" + Formatter.format(current, DD_MMMM_YYYY_hh_mm_aa));
            val difference = current.timeInMillis - calendarDate.timeInMillis
            val year = difference / YEAR
            val month = difference / MONTH
            val day = difference / DAY
            val week = difference / WEEK
            val hour = difference / HOUR
            val minute = difference / MINUTE
            val second = difference / SECOND
            var time: String? = null
            if (year > 0) time = String.format(
                "%d %s",
                year,
                if (year > 1) context.getString(R.string.years_ago) else context.getString(R.string.year_ago)
            ) else if (month > 0) time = String.format(
                "%d %s",
                month,
                if (month > 1) context.getString(R.string.months_ago) else context.getString(R.string.month_ago)
            ) else if (week > 0) time = java.lang.String.format(
                locale,
                "%d %s",
                week,
                if (week > 1) context.getString(R.string.weeks_ago) else context.getString(R.string.week_ago)
            ) else if (day > 0) time = String.format(
                "%d %s",
                day,
                if (day > 1) context.getString(R.string.days_ago) else context.getString(R.string.day_ago)
            ) else if (hour > 0) time = String.format(
                "%d %s",
                hour,
                if (hour > 1) context.getString(R.string.hours_ago) else context.getString(R.string.hour_ago)
            ) else if (minute > 0) time = String.format(
                "%d %s",
                minute,
                if (minute > 1) context.getString(R.string.minutes_ago) else context.getString(R.string.minute_ago)
            ) else if (second > 0) time = String.format(
                "%d %s",
                second,
                if (second > 1) context.getString(R.string.seconds_ago) else context.getString(R.string.second_ago)
            )
            return if (time != null) String.format(time) else context.getString(R.string.just_now)
            //                return String.format(formattedString, time);
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1 // Adding 1 because Calendar months are 0-based
    }

    fun getCurrentDate(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

}



fun Formatter.getTeeTime(time: String): String? {
    return format(time, hh_mm__ss_aa, hh_mm_aa, false)
}
