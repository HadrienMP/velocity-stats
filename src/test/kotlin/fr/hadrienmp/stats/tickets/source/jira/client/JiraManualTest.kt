package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.stats.tickets.source.jira.credentials
import fr.hadrienmp.stats.tickets.source.jira.jiraTickets
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import java.io.File
import java.nio.charset.Charset.defaultCharset
import java.time.LocalDate
import java.time.ZonedDateTime

fun main(args: Array<String>) {
    val credentials = credentials(args)
    val project = args.first { it.matches(Regex("jira.project=.*")) }.split("=")[1]
    val jiraHost = args.first { it.matches(Regex("jira.host=.*")) }.split("=")[1]

    val jiraTickets = jiraTickets(credentials, jiraHost, project, LocalDate.now().minusMonths(6).withDayOfMonth(1))

    Parser.parse<Response>(jiraTickets.body())!!.let { println(it) }

    File("src/test/kotlin/fr/hadrienmp/stats/tickets/source/jira/client/tickets.json")
            .writeText(jiraTickets.body(), defaultCharset())
}

data class Response(val startAt: Int,
                    val maxResults: Int,
                    val total: Int,
                    val issues: List<Ticket>)

data class Ticket(val fields: Fields)

data class Fields(val status: Status? = null, val resolutiondate: String? = null, val customfield_10002: Float = 0f)

data class Status(val statusCategory: StatusCategory)

data class StatusCategory(val key: String)
