package fr.hadrienmp.fwgstats.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period

data class Ticket(
        val createDate: LocalDate,
        val type: TicketType,
        val points: Int? = null,
        val finishDate: LocalDate? = null,
        val prApprobationDate: LocalDate? = null,
        val devStartDate: LocalDate? = null,
        val devFinishDate: LocalDate? = null
) {

    fun finishMonth() = finishDate?.withDayOfMonth(1)
    fun finishWeek() = finishDate?.with(DayOfWeek.FRIDAY)

    fun cycleTime() = finishDate?.until(createDate)?.negated()
    fun devTime() = period(devStartDate, devFinishDate)
    fun startToCloseTime() = period(devStartDate, finishDate)

    private fun period(from: LocalDate?, to: LocalDate?): Period? {
        if (from == null || to == null) return null
        return from.until(to)
    }
}
