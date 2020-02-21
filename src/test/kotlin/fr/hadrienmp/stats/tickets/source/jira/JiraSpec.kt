package fr.hadrienmp.stats.tickets.source.jira

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType
import fr.hadrienmp.stats.tickets.source.jira.client.Fields
import fr.hadrienmp.stats.tickets.source.jira.client.IssueType
import fr.hadrienmp.stats.tickets.source.jira.client.PageClient
import fr.hadrienmp.stats.tickets.source.jira.client.Response
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate
import java.time.ZonedDateTime
import fr.hadrienmp.stats.tickets.source.jira.client.Ticket as JiraTicket

// TODO HMP: 21/02/2020 date à minuit +2 -> le jour d'avant ?
internal class JiraSpec : StringSpec({
    "map the jira page response to tickets" {
        val pageClient = mock<PageClient> {
            on { ticketsAfter(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate()) } doReturn Response(
                    startAt = 0,
                    maxResults = 50,
                    total = 4,
                    issues = listOf(
                            JiraTicket(Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    estimate = 3.0f,
                                    type = IssueType("Récit"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")),
                            JiraTicket(Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Bogue"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")),
                            JiraTicket(Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Bogue"))),
                            JiraTicket(Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Autre"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200"))
                    )
            )
        }
        val ticketSource = Jira(pageClient)
        val tickets = ticketSource.after(ZonedDateTime.parse("2010-01-01T00:00:00Z"))
        assertThat(tickets).containsOnly(
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.FEATURE,
                        finishDate = LocalDate.parse("2019-12-01"),
                        points = 3),
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.BUG,
                        finishDate = LocalDate.parse("2019-12-01")),
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.BUG),
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.UNKNOWN,
                        finishDate = LocalDate.parse("2019-12-01")))

    }
})

