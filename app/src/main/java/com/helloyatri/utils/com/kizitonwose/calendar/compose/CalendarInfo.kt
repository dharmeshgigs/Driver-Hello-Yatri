package com.kizitonwose.calendar.compose

import androidx.compose.runtime.Immutable
import com.kizitonwose.calendar.core.OutDateStyle
import java.time.DayOfWeek

@Immutable
internal data class CalendarInfo(
    val indexCount: Int,
    private val firstDayOfWeek: DayOfWeek? = null,
    private val outDateStyle: OutDateStyle? = null,
)
