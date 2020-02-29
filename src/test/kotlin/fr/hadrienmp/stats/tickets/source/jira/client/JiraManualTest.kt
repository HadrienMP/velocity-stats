package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.lib.web.AppArguments
import java.time.LocalDate

fun main(args: Array<String>) {
    val credentials = Credentials.from(AppArguments(args))!!
    val project = args.first { it.matches(Regex("jira.project=.*")) }.split("=")[1]
    val jiraHost = args.first { it.matches(Regex("jira.host=.*")) }.split("=")[1]

    val jiraTicketsResponse = DefaultPageClient(credentials, jiraHost, project).ticketsAfter(LocalDate.parse("2020-01-01"), 0)

    println(jiraTicketsResponse)
}

