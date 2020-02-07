package fr.hadrienmp.fwgstats.api.web

import fr.hadrienmp.fwgstats.domain.Ticket
import fr.hadrienmp.fwgstats.domain.countBy
import fr.hadrienmp.fwgstats.domain.statsOf
import fr.hadrienmp.fwgstats.domain.timesInDaysByPoint
import fr.hadrienmp.fwgstats.tickets.source.pivotal.Pivotal
import fr.hadrienmp.fwgstats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.fwgstats.tickets.source.pivotal.client.pivotalClientFrom
import fr.hadrienmp.lib.web.Port
import fr.hadrienmp.lib.web.ThymeleafTemplates
import fr.hadrienmp.lib.web.WebApp
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

        val tickets = Pivotal(pivotalClient).after(ZonedDateTime.now().minusMonths(6).withDayOfMonth(1))
        it.get("/stats/tickets-finished-per-month") {
            it.json(statsOf(tickets, Ticket::finishMonth))
        }
        it.get("/stats/tickets-finished-per-week") {
            it.json(statsOf(tickets, Ticket::finishWeek))
        }
        it.get("/stats/cycle-times") {
            it.json(tickets.timesInDaysByPoint(Ticket::cycleTime))
        }
        it.get("/stats/dev-times") {
            it.json(tickets.timesInDaysByPoint(Ticket::devTime))
        }
        it.get("/stats/start-to-close-times") {
            it.json(tickets.timesInDaysByPoint(Ticket::startToCloseTime))
        }
        it.get("/stats/points-repartition") {
            it.json(tickets
                    .countBy(Ticket::points)
                    .mapKeys { "${it.key} points" })
        }
    }.withStaticFolder("/webapp")
}

