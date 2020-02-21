package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.stats.tickets.source.jira.*
import java.io.File
import java.nio.charset.Charset.defaultCharset
import java.time.LocalDate

fun main(args: Array<String>) {
    val credentials = credentials(args)
    val project = args.first { it.matches(Regex("jira.project=.*")) }.split("=")[1]
    val jiraHost = args.first { it.matches(Regex("jira.host=.*")) }.split("=")[1]

    val jiraTicketsResponse = PageClient(credentials, jiraHost, project).ticketsAfter(LocalDate.now())

    println(jiraTicketsResponse.parsed)

    File("src/test/kotlin/fr/hadrienmp/stats/tickets/source/jira/client/tickets.json")
            .writeText(jiraTicketsResponse.raw.body(), defaultCharset())
}

