package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.stats.tickets.source.jira.Credentials
import fr.hadrienmp.stats.tickets.source.jira.jiraTickets
import java.io.File
import java.nio.charset.Charset.defaultCharset
import java.time.LocalDate

fun main(args: Array<String>) {
    val userName = args.first { it.matches(Regex("jira.user=.*")) }.split("=")[1]
    val password = args.first { it.matches(Regex("jira.password=.*")) }.split("=")[1]
    val project = args.first { it.matches(Regex("jira.project=.*")) }.split("=")[1]
    val jiraHost = args.first { it.matches(Regex("jira.host=.*")) }.split("=")[1]

    val jiraTickets = jiraTickets(Credentials(userName, password), project, LocalDate.now().minusMonths(6).withDayOfMonth(1), jiraHost)
    File("src/test/kotlin/fr/hadrienmp/stats/tickets/source/jira/client/tickets.json")
            .writeText(jiraTickets.body(), defaultCharset())
}
