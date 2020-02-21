package fr.hadrienmp.stats.tickets.source.jira

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType
import fr.hadrienmp.stats.domain.Tickets
import fr.hadrienmp.stats.tickets.source.jira.client.PageClient
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Jira(private val pageClient: PageClient) : Tickets {
    override fun after(analysisStartDate: ZonedDateTime): List<Ticket> {
        return pageClient.ticketsAfter(analysisStartDate.toLocalDate())
                .issues.map {
            Ticket(
                    createDate = LocalDate.parse(it.fields.created, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")),
                    points = it.fields.estimate?.toInt(),
                    type = when (it.fields.type.name) {
                        "Récit" -> TicketType.FEATURE
                        "Bogue" -> TicketType.BUG
                        else -> TicketType.UNKNOWN
                    },
                    finishDate = it.fields.acceptedAt?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")) }
            )
        }
    }
}