package fr.hadrienmp.stats.domain

import java.time.ZonedDateTime

interface TicketSource {
    fun doneTicketsAfter(analysisStartDate: ZonedDateTime): List<DoneTicket>
}