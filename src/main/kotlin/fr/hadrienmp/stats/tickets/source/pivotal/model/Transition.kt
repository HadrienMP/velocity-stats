package fr.hadrienmp.stats.tickets.source.pivotal.model

import com.beust.klaxon.Json
import java.time.ZonedDateTime

data class Transition(val state: String,
                 @Json("occurred_at")
                 val date: ZonedDateTime): Comparable<Transition> {
    override fun compareTo(other: Transition): Int = date.compareTo(other.date)
}