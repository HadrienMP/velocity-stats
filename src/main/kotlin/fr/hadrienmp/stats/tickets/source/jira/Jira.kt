package fr.hadrienmp.stats.tickets.source.jira

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketSource
import fr.hadrienmp.stats.tickets.source.jira.client.FixVersion
import fr.hadrienmp.stats.tickets.source.jira.client.PageClient
import java.time.ZonedDateTime
import fr.hadrienmp.stats.tickets.source.jira.client.Ticket as JiraTicket

class Jira(private val pageClient: PageClient) : TicketSource {
    override fun after(analysisStartDate: ZonedDateTime): List<Ticket> {
        return generateSequence(0) { it + 50 }
                .map { pageClient.ticketsAfter(analysisStartDate.toLocalDate(), it) }
                .takeWhile { it.issues.isNotEmpty() }
                .flatMap { it.issues.asSequence() }
                .filterNot { it.fields.fixVersions.contains(FixVersion("Pivotal -> Jira")) }
                .map(JiraTicket::toCore)
                .toList()
    }
}