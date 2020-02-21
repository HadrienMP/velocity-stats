package fr.hadrienmp.stats.tickets.source.jira

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import fr.hadrienmp.stats.tickets.source.pivotal.Parser
import java.time.LocalDate

class PageClient(val credentials: Credentials, private val jiraHost: String, val project: String) {
    fun ticketsAfter(localDate: LocalDate) = Parser.parse<Response>(
            JdkRequest("$jiraHost/rest/api/latest/search")
                    .header("authorization", "Basic ${credentials.toBase64()}")
                    .header("content-type", "application/json")
                    .method(Request.POST)
                    .body()
                    .set("""{"jql": "${jql(localDate)}"}""")
                    .back()
                    .fetch()
                    .body())

    private fun jql(localDate: LocalDate) = "project = $project " +
            "AND created >= ${localDate.minusMonths(6).withDayOfMonth(1)} " +
            "ORDER BY created DESC"
}
