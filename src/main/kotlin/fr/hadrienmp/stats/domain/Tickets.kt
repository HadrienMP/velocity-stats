package fr.hadrienmp.stats.domain

import java.time.ZonedDateTime

interface Tickets {
    fun after(date: ZonedDateTime): List<Ticket>
}