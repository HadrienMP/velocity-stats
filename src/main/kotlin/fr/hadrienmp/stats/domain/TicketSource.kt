package fr.hadrienmp.stats.domain

import java.time.ZonedDateTime

interface TicketSource {
    fun after(analysisStartDate: ZonedDateTime): List<Ticket>
}