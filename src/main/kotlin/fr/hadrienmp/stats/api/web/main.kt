package fr.hadrienmp.stats.api.web

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.countBy
import fr.hadrienmp.stats.domain.statsOf
import fr.hadrienmp.stats.domain.timesInDaysByPoint
import fr.hadrienmp.stats.tickets.source.pivotal.Pivotal
import fr.hadrienmp.stats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.stats.tickets.source.pivotal.client.pivotalClientFrom
import fr.hadrienmp.lib.web.Port
import fr.hadrienmp.lib.web.ThymeleafTemplates
import fr.hadrienmp.lib.web.WebApp
import fr.hadrienmp.stats.tickets.source.TicketsCache
import java.time.Duration
import java.time.ZonedDateTime

fun main(args: Array<String>) {
    webapp(Port(args), pivotalClientFrom(args)).start()
}

fun webapp(port: Port, pivotalClient: PivotalClient): WebApp {

    ThymeleafTemplates("webapp/").enable()

    return WebApp(port).withRoutes {
        it.get("/") {
            it.renderThymeleaf("plots.html")
        }

        val ticketsCache = TicketsCache(Pivotal(pivotalClient), Duration.ofMinutes(5))
        val analysisStartDate = ZonedDateTime.now().minusMonths(6).withDayOfMonth(1)
        it.get("/stats/tickets-finished-per-month") {
            it.json(statsOf(ticketsCache.after(analysisStartDate), Ticket::finishMonth))
        }
        it.get("/stats/tickets-finished-per-week") {
            it.json(statsOf(ticketsCache.after(analysisStartDate), Ticket::finishWeek))
        }
        it.get("/stats/cycle-times") {
            it.json(ticketsCache.after(analysisStartDate).timesInDaysByPoint(Ticket::cycleTime))
        }
        it.get("/stats/dev-times") {
            it.json(ticketsCache.after(analysisStartDate).timesInDaysByPoint(Ticket::devTime))
        }
        it.get("/stats/start-to-close-times") {
            it.json(ticketsCache.after(analysisStartDate).timesInDaysByPoint(Ticket::startToCloseTime))
        }
        it.get("/stats/points-repartition") {
            it.json(ticketsCache.after(analysisStartDate)
                    .countBy(Ticket::points)
                    .mapKeys { "${it.key} points" })
        }
    }.withStaticFolder("/webapp")
}

