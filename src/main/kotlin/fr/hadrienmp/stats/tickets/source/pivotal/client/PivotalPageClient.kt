package fr.hadrienmp.stats.tickets.source.pivotal.client

import com.jcabi.http.request.JdkRequest
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicketsResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PivotalPageClient(private val token: String, projectId: Int) {
    private val apiUrl = "https://www.pivotaltracker.com/services/v5/projects/${projectId}/stories?" +
            "offset=%s" +
            "&limit=100" +
            "&envelope=true" +
            "&with_state=accepted" +
            "&fields=%%3Adefault%%2Ctransitions%%2Ccycle_time_details"

    fun page(offset: Int, date: ZonedDateTime? = null): PivotalTicketsResponse {
        val jsonResponse = JdkRequest(pageUrl(offset = offset, acceptedAfter = date))
                .header("X-TrackerToken", token)
                .fetch()
                .body()
        return Parser.parse<PivotalTicketsResponse>(jsonResponse)!!
    }

    private fun pageUrl(offset: Int, acceptedAfter: ZonedDateTime? = null): String {
        if (acceptedAfter != null) {
            return apiUrl.format(offset) + "&accepted_after=" + acceptedAfter.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
        return apiUrl.format(offset)
    }
}