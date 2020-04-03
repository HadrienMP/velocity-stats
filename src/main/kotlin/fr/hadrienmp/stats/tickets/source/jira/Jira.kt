package fr.hadrienmp.stats.tickets.source.jira

import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.FutureTicket
import fr.hadrienmp.stats.domain.TicketSource
import fr.hadrienmp.stats.tickets.source.jira.client.PageClient
import java.time.ZonedDateTime
import fr.hadrienmp.stats.tickets.source.jira.client.Ticket as JiraTicket

class Jira(private val pageClient: PageClient) : TicketSource {
    override fun doneTicketsAfter(analysisStartDate: ZonedDateTime): List<DoneTicket> {
        return generateSequence(0) { it + 50 }
                .map { pageClient.ticketsAfter(analysisStartDate.toLocalDate(), it) }
                .takeWhile { it.issues.isNotEmpty() }
                .flatMap { it.issues.asSequence() }
                .map(JiraTicket::toCore)
                .toList()
    }
    override fun future(): List<FutureTicket> {
        return generateSequence(0) { it + 50 }
                .map { pageClient.future(it) }
                .takeWhile { it.issues.isNotEmpty() }
                .flatMap { it.issues.asSequence() }
                .map(JiraTicket::toFutureTicket)
                .toList()
    }
}