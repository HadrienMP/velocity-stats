package fr.hadrienmp.stats.api.web

import fr.hadrienmp.lib.web.AppArguments
import fr.hadrienmp.lib.web.Port
import fr.hadrienmp.lib.web.ThymeleafTemplates
import fr.hadrienmp.lib.web.WebApp
import fr.hadrienmp.stats.domain.*
import fr.hadrienmp.stats.tickets.source.TicketSourceCache
import fr.hadrienmp.stats.tickets.source.jira.Jira
import fr.hadrienmp.stats.tickets.source.pivotal.Pivotal
import fr.hadrienmp.stats.tickets.source.pivotal.client.pivotalClientFrom
import io.javalin.http.Context
import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import java.time.Duration
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    val appArguments = AppArguments(args)
    val jira = jiraPageClientFrom(appArguments)?.let(::Jira)
    val pivotal = Pivotal(pivotalClientFrom(args))
    val ticketSources = listOfNotNull(pivotal, jira)
    println(ticketSources)
    webapp(Port(args), ticketSources).start()
}

fun webapp(port: Port, ticketSources: List<TicketSource>): WebApp {
    ThymeleafTemplates("webapp/").enable()

    return WebApp(port, "/webapp").withRoutes { route ->
        val tickets = TicketSourceCache(Duration.ofMinutes(5), ticketSources)

        route.get("/") {
            val numberOfMonthsToAnalyze = it.queryParam(key = "period")?.toLong() ?: 3
            it.cookieStore("period", numberOfMonthsToAnalyze)
            it.render("plots.html", mapOf(Pair("period", numberOfMonthsToAnalyze)))
        }

        route.get("/projection") { ctx ->
            val numberOfMonthsToAnalyze = ctx.queryParam(key = "period")?.toLong() ?: 3
            ctx.cookieStore("period", numberOfMonthsToAnalyze)
            val doneTickets = tickets
                    .doneTicketsAfter(now().minusMonths(numberOfMonthsToAnalyze))
                    .filter { it.type == TicketType.FEATURE }
                    .groupBy { ticket ->
                        ticket.finishDate.withDayOfMonth(now().dayOfMonth)
                    }.mapValues { it.value.size }
            println(doneTickets)
            val storiesByMonth = SummaryStatistics()
            doneTickets.forEach { storiesByMonth.addValue(it.value.toDouble()) }

            ctx.render("projection.html", mapOf(
                    Pair("period", numberOfMonthsToAnalyze),
                    Pair("meanStories", storiesByMonth.mean.roundToInt()),
                    Pair("tickets", tickets.future())))
        }

        route.get("/stats/tickets-finished-per-month") {
            it.json(statsOf(tickets.doneTicketsAfter(analysisStartDate(it)), DoneTicket::finishMonth))
        }
        route.get("/stats/tickets-finished-per-week") {
            it.json(statsOf(tickets.doneTicketsAfter(analysisStartDate(it)), DoneTicket::finishWeek))
        }
        route.get("/stats/cycle-times") {
            it.json(tickets.doneTicketsAfter(analysisStartDate(it)).timesInDaysByPoint(DoneTicket::cycleTime))
        }
        route.get("/stats/points-repartition") { context ->
            context.json(tickets.doneTicketsAfter(analysisStartDate(context))
                    .countBy(DoneTicket::points)
                    .mapKeys { "${it.key} points" })
        }
        route.get("/stats/types-repartition") { context ->
            val ticketsByTypes = tickets.doneTicketsAfter(analysisStartDate(context)).countBy(DoneTicket::type)
            context.json(ticketsByTypes)
        }
    }
}

private fun analysisStartDate(it: Context): ZonedDateTime {
    return now()
            .minusMonths(it.cookieStore("period"))
            .withDayOfMonth(1)
            .truncatedTo(ChronoUnit.DAYS)
}

