package fr.hadrienmp.fwgstats.domain

import fr.hadrienmp.fwgstats.domain.Ticket
import java.time.LocalDate
import java.time.ZonedDateTime

interface Tickets {
    fun all(): List<Ticket>
    fun after(date: ZonedDateTime): List<Ticket>
}