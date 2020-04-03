package fr.hadrienmp.stats.tickets.source.jira

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType
import fr.hadrienmp.stats.domain.aTicket
import fr.hadrienmp.stats.tickets.source.jira.client.Fields
import fr.hadrienmp.stats.tickets.source.jira.client.IssueType
import fr.hadrienmp.stats.tickets.source.jira.client.PageClient
import fr.hadrienmp.stats.tickets.source.jira.client.Response
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate
import java.time.ZonedDateTime
import fr.hadrienmp.stats.tickets.source.jira.client.Ticket as JiraTicket

internal class JiraSpec : StringSpec({
    "map the jira page response to tickets" {
        val pageClient = mock<PageClient> {
            on { ticketsAfter(eq(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate()), any()) } doReturn emptyPage()
            on { ticketsAfter(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate(), 0) } doReturn Response(
                    startAt = 0,
                    maxResults = 50,
                    total = 4,
                    issues = listOf(
                            JiraTicket(key = "first", fields = Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    estimate = 3.0f,
                                    type = IssueType("Récit"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")),
                            JiraTicket(key = "second", fields = Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Bogue"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")),
                            JiraTicket(key = "third",
                                    fields = Fields(
                                            created = "2019-10-01T16:52:42.000+0200",
                                            type = IssueType("Bogue"),
                                            acceptedAt = "2019-10-01T16:52:42.000+0200")
                            ),
                            JiraTicket(key = "third", fields = Fields(
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
                aTicket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.BUG),
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.UNKNOWN,
                        finishDate = LocalDate.parse("2019-12-01")))

    }
    "dates at midnight" {
        val pageClient = mock<PageClient> {
            on { ticketsAfter(eq(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate()), any()) } doReturn emptyPage()
            on { ticketsAfter(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate(), 0) } doReturn Response(
                    startAt = 0,
                    maxResults = 50,
                    total = 4,
                    issues = listOf(
                            JiraTicket(key = "first", fields = Fields(
                                    created = "2019-10-01T00:00:00.000+0200",
                                    type = IssueType("Récit"),
                                    acceptedAt = "2019-10-01T16:52:42.000+0200")))
            )
        }
        val ticketSource = Jira(pageClient)
        val tickets = ticketSource.after(ZonedDateTime.parse("2010-01-01T00:00:00Z"))
        assertThat(tickets).containsOnly(
                aTicket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.FEATURE))

    }
    "return all the tickets when there is multiple pages" {
        val pageClient = mock<PageClient> {
            on { ticketsAfter(eq(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate()), any()) } doReturn emptyPage()
            on { ticketsAfter(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate(), 0) } doReturn Response(
                    startAt = 0,
                    maxResults = 50,
                    total = 52,
                    issues = listOf(
                            JiraTicket(key = "first", fields = Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    estimate = 3.0f,
                                    type = IssueType("Récit"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")),
                            JiraTicket(key = "second", fields = Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Bogue"),
                                    acceptedAt = "2019-12-01T16:52:42.000+0200")))
            )
            on { ticketsAfter(ZonedDateTime.parse("2010-01-01T00:00:00Z").toLocalDate(), 50) } doReturn Response(
                    startAt = 50,
                    maxResults = 50,
                    total = 52,
                    issues = listOf(
                            JiraTicket(key = "third", fields = Fields(
                                    created = "2019-10-01T16:52:42.000+0200",
                                    type = IssueType("Bogue"),
                                    acceptedAt = "2019-10-01T16:52:42.000+0200")),
                            JiraTicket(key = "fourth", fields = Fields(
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
                aTicket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.BUG),
                Ticket(createDate = LocalDate.parse("2019-10-01"),
                        type = TicketType.UNKNOWN,
                        finishDate = LocalDate.parse("2019-12-01")))
    }
})

private fun emptyPage(): Response {
    return Response(
            startAt = 0,
            maxResults = 50,
            total = 4,
            issues = emptyList()
    )
}

