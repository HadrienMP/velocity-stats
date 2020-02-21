package fr.hadrienmp.stats.tickets.source.jira.client

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import java.net.URLEncoder
import java.time.LocalDate

interface PageClient {
    fun ticketsAfter(localDate: LocalDate, offset: Int): Response
}

fun jiraPageClientFrom(args: Array<String>): DefaultPageClient {
    val project = args.first { it.matches(Regex("jira.project=.*")) }.split("=")[1]
    val jiraHost = args.first { it.matches(Regex("jira.host=.*")) }.split("=")[1]
    val credentials = Credentials.from(args)
    return DefaultPageClient(credentials, jiraHost, project)
}

class DefaultPageClient(val credentials: Credentials, private val jiraHost: String, val project: String) : PageClient {
    override fun ticketsAfter(localDate: LocalDate, offset: Int): Response {
        val url = "$jiraHost/rest/api/latest/search?startAt=$offset&maxResults=50&jql=${URLEncoder.encode(jql(localDate), Charsets.UTF_8.name())}"
        return Parser.parse<Response>(
                JdkRequest(url)
                        .header("authorization", "Basic ${credentials.toBase64()}")
                        .header("content-type", "application/json")
                        .fetch()
                        .body())!!
    }

    private fun jql(localDate: LocalDate) = "project=$project " +
            "AND resolutiondate>=${localDate} " +
            "ORDER BY created DESC"
}
