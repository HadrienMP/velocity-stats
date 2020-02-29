package fr.hadrienmp.stats.api.web

import fr.hadrienmp.lib.web.Port
import fr.hadrienmp.lib.web.ThymeleafTemplates
import fr.hadrienmp.lib.web.WebApp
import fr.hadrienmp.stats.domain.*
import fr.hadrienmp.stats.tickets.source.TicketSourceCache
import fr.hadrienmp.stats.tickets.source.jira.Jira
import fr.hadrienmp.stats.tickets.source.jira.client.jiraPageClientFrom
import fr.hadrienmp.stats.tickets.source.pivotal.Pivotal
import fr.hadrienmp.stats.tickets.source.pivotal.client.pivotalClientFrom
import java.time.Duration
import java.time.ZonedDateTime

fun main(args: Array<String>) {
    webapp(Port(args), Pivotal(pivotalClientFrom(args)), Jira(jiraPageClientFrom(args))).start()
}

fun webapp(port: Port, vararg ticketSources: TicketSource): WebApp {
    ThymeleafTemplates("webapp/").enable()

    return WebApp(port).withRoutes { javalin ->
        javalin.get("/") {
            it.renderThymeleaf("plots.html")
        }

        val tickets = TicketSourceCache(Duration.ofMinutes(5), ticketSources)
        val analysisStartDate = ZonedDateTime.now().minusMonths(5).withDayOfMonth(1)


        javalin.get("/stats/tickets-finished-per-month") {
            it.json(statsOf(tickets.after(analysisStartDate), Ticket::finishMonth))
        }
        javalin.get("/stats/tickets-finished-per-week") {
            it.json(statsOf(tickets.after(analysisStartDate), Ticket::finishWeek))
        }
        javalin.get("/stats/cycle-times") {
            it.json(tickets.after(analysisStartDate).timesInDaysByPoint(Ticket::cycleTime))
        }
        javalin.get("/stats/points-repartition") { context ->
            context.json(tickets.after(analysisStartDate)
                    .countBy(Ticket::points)
                    .mapKeys { "${it.key} points" })
        }
    }.withStaticFolder("/webapp")
}

