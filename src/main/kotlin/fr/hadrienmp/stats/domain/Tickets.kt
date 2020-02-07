package fr.hadrienmp.stats.domain

import java.time.ZonedDateTime

interface Tickets {
    fun all(): List<Ticket>
    fun after(date: ZonedDateTime): List<Ticket>
}