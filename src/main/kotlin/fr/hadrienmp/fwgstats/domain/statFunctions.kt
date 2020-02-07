package fr.hadrienmp.fwgstats.domain

import java.time.LocalDate
import java.time.Period

fun statsOf(tickets: List<Ticket>, property: (Ticket) -> LocalDate?): Map<String, Map<LocalDate, Int>> {
    return mapOf(
            Pair("stories", tickets.countBy(property) { it.type == TicketType.FEATURE }),
            Pair("bugs", tickets.countBy(property) { it.type == TicketType.BUG }),
            Pair("unknowns", tickets.countBy(property) { it.type == TicketType.UNKNOWN }),
            Pair("points", tickets.countPointsBy(property)),
            Pair("tickets", tickets.countBy(property))
    )
}

fun featuresFinishedByMonth(tickets: List<Ticket>): Map<LocalDate, Int> {
    return tickets.countBy(Ticket::finishMonth) { it.type == TicketType.FEATURE }
}

fun <OBJECT, FIELD : Any> List<OBJECT>.countBy(field: (OBJECT) -> FIELD?, matching: (OBJECT) -> Boolean): Map<FIELD, Int> {
    return this.filter(matching).countBy(field)
}

fun <OBJECT, FIELD : Any> List<OBJECT>.countBy(by: (OBJECT) -> FIELD?): Map<FIELD, Int> {
    return this.mapNotNull(by)
            .groupBy { it }
            .mapValues { it.value.size }
}

fun <FIELD : Any> List<Ticket>.countPointsBy(field: (Ticket) -> FIELD?): Map<FIELD, Int> {
    return this.groupBy(field)
            .filterKeys { it != null }
            .mapNotNull { Pair(it.key!!, it.value.sumPoints()) }
            .toMap()
}

private fun List<Ticket>.sumPoints() = this.mapNotNull { it.points }.sum()
fun List<Ticket>.timesInDaysByPoint(timeFunction: (Ticket) -> Period?): Map<Int?, List<Int>> {
    return filter { it.points != null }
            .groupBy { it.points }
            .mapValues {
                it.value.mapNotNull(timeFunction).map { it.days }
            }
}