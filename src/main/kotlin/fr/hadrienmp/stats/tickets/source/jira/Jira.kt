package fr.hadrienmp.stats.tickets.source.jira

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import java.time.LocalDate
import com.jcabi.http.Response as HttpResponse

class JiraTicketsResponse(val raw: HttpResponse) {
    val parsed = Parser.parse<Response>(raw.body())
}

class PageClient(val credentials: Credentials, private val jiraHost: String, val project: String) {
    fun ticketsAfter(localDate: LocalDate): JiraTicketsResponse {
        val jql = "project = $project " +
                "AND created >= ${localDate.minusMonths(6).withDayOfMonth(1)} " +
                "ORDER BY created DESC"
        return JiraTicketsResponse(
                JdkRequest("$jiraHost/rest/api/latest/search")
                        .header("authorization", "Basic ${credentials.toBase64()}")
                        .header("content-type", "application/json")
                        .method(Request.POST)
                        .body()
                        .set("""{"jql": "$jql"}""")
                        .back()
                        .fetch()!!)
    }
}
