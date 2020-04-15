package fr.hadrienmp.stats.domain

import java.time.LocalDate
import java.time.Period

fun statsOf(tickets: List<DoneTicket>, property: (DoneTicket) -> LocalDate?): Map<String, Map<LocalDate, Int>> {
    return mapOf(
            Pair("stories", tickets.countBy(property) { it.type == TicketType.FEATURE }),
            Pair("bugs", tickets.countBy(property) { it.type == TicketType.BUG }),
            Pair("chores", tickets.countBy(property) { it.type == TicketType.CHORE }),
            Pair("points", tickets.countPointsBy(property)),
            Pair("tickets", tickets.countBy(property))
    )
}

fun featuresFinishedByMonth(tickets: List<DoneTicket>): Map<LocalDate, Int> {
    return tickets.countBy(DoneTicket::finishMonth) { it.type == TicketType.FEATURE }
}

fun <OBJECT, FIELD : Any> List<OBJECT>.countBy(field: (OBJECT) -> FIELD?, matching: (OBJECT) -> Boolean): Map<FIELD, Int> {
    return this.filter(matching).countBy(field)
}

fun <OBJECT, FIELD : Any> List<OBJECT>.countBy(by: (OBJECT) -> FIELD?): Map<FIELD, Int> {
    return this.mapNotNull(by)
            .groupBy { it }
            .mapValues { it.value.size }
}

fun <FIELD : Any> List<DoneTicket>.countPointsBy(field: (DoneTicket) -> FIELD?): Map<FIELD, Int> {
    return this.groupBy(field)
            .filterKeys { it != null }
            .mapNotNull { Pair(it.key!!, it.value.sumPoints()) }
            .toMap()
}

private fun List<DoneTicket>.sumPoints() = this.mapNotNull { it.points }.sum()
fun List<DoneTicket>.timesInDaysByPoint(timeFunction: (DoneTicket) -> Period?): Map<Int?, List<Int>> {
    return filter { it.points != null }
            .groupBy { it.points }
            .mapValues {
                it.value.mapNotNull(timeFunction).map { it.days }
            }
}