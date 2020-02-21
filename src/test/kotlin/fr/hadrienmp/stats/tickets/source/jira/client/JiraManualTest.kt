package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.stats.tickets.source.jira.credentials
import fr.hadrienmp.stats.tickets.source.jira.jiraTickets
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicketsResponse
import java.io.File
import java.nio.charset.Charset.defaultCharset
import java.time.LocalDate

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

data class Ticket(val fields: Field)

data class Field(val status: Status? = null)

data class Status(val statusCategory: StatusCategory)

data class StatusCategory(val key: String)
