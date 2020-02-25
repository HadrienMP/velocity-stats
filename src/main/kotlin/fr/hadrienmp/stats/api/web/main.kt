package fr.hadrienmp.stats.api.web

import fr.hadrienmp.lib.web.Port
import fr.hadrienmp.lib.web.ThymeleafTemplates
import fr.hadrienmp.lib.web.WebApp
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.countBy
import fr.hadrienmp.stats.domain.statsOf
import fr.hadrienmp.stats.domain.timesInDaysByPoint
import fr.hadrienmp.stats.tickets.source.TicketSourceCache
import fr.hadrienmp.stats.tickets.source.jira.Jira
import fr.hadrienmp.stats.tickets.source.jira.client.DefaultPageClient
import fr.hadrienmp.stats.tickets.source.jira.client.jiraPageClientFrom
import fr.hadrienmp.stats.tickets.source.pivotal.Pivotal
import fr.hadrienmp.stats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.stats.tickets.source.pivotal.client.pivotalClientFrom
import java.time.Duration
import java.time.ZonedDateTime

fun main(args: Array<String>) {
    webapp(Port(args), pivotalClientFrom(args), jiraPageClientFrom(args)).start()
}

fun webapp(port: Port, pivotalClient: PivotalClient, defaultPageClient: DefaultPageClient): WebApp {

    ThymeleafTemplates("webapp/").enable()

    return WebApp(port).withRoutes {
        it.get("/") {
            it.renderThymeleaf("plots.html")
        }

        val tickets = TicketSourceCache(Duration.ofMinutes(5), Pivotal(pivotalClient), Jira(defaultPageClient))
        val analysisStartDate = ZonedDateTime.now().minusMonths(5).withDayOfMonth(1)


        it.get("/stats/tickets-finished-per-month") {
            it.json(statsOf(tickets.after(analysisStartDate), Ticket::finishMonth))
        }
        it.get("/stats/tickets-finished-per-week") {
            it.json(statsOf(tickets.after(analysisStartDate), Ticket::finishWeek))
        }
        it.get("/stats/cycle-times") {
            it.json(tickets.after(analysisStartDate).timesInDaysByPoint(Ticket::cycleTime))
        }
        it.get("/stats/points-repartition") {
            it.json(tickets.after(analysisStartDate)
                    .countBy(Ticket::points)
                    .mapKeys { "${it.key} points" })
        }
    }.withStaticFolder("/webapp")
}

