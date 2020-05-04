package fr.hadrienmp.stats.tickets.source.jira.client

import com.jcabi.http.request.JdkRequest
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.time.LocalDate

interface PageClient {
    fun ticketsAfter(localDate: LocalDate, offset: Int): Response
    fun future(offset: Int): Response
}

class DefaultPageClient(val credentials: Credentials, private val jiraHost: String, val project: String, private val jqlCustomization: String = "") : PageClient {
    companion object {
        val log = LoggerFactory.getLogger(DefaultPageClient::class.java)
    }
    override fun ticketsAfter(localDate: LocalDate, offset: Int): Response {
        val url = "$jiraHost/rest/api/2/search?startAt=$offset&maxResults=50&jql=${URLEncoder.encode(jql(localDate), Charsets.UTF_8.name())}"
        val jsonResponse = JdkRequest(url)
                .header("authorization", "Basic ${credentials.toBase64()}")
                .header("content-type", "application/json")
                .fetch()
                .body()
        log.info(jsonResponse);
        return Parser.parse<Response>(jsonResponse)!!
    }

    private fun jql(localDate: LocalDate) = "project = $project AND resolved >= $localDate $jqlCustomization"

    override fun future(offset: Int): Response {
        val jql = "project = FWG " +
                "AND issuetype = Story " +
                "AND resolved is EMPTY " +
                "AND Sprint in (openSprints(), futureSprints()) " +
                "AND (\"Point d'effort\" != 0 OR \"Point d'effort\" is EMPTY) " +
                "ORDER BY cf[10200] ASC"
        val url = "$jiraHost/rest/api/2/search?startAt=$offset&maxResults=50&jql=${URLEncoder.encode(jql, Charsets.UTF_8.name())}"
        val jsonResponse = JdkRequest(url)
                .header("authorization", "Basic ${credentials.toBase64()}")
                .header("content-type", "application/json")
                .fetch()
                .body()
        return Parser.parse<Response>(jsonResponse)!!
    }
}
