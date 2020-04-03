package fr.hadrienmp.stats.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period

data class DoneTicket(
        val createDate: LocalDate,
        val type: TicketType,
        val points: Int? = null,
        val finishDate: LocalDate
) {
    fun finishMonth() = finishDate.withDayOfMonth(1)
    fun finishWeek() = finishDate.with(DayOfWeek.FRIDAY)
    fun cycleTime() = finishDate.until(createDate)?.negated()
}
