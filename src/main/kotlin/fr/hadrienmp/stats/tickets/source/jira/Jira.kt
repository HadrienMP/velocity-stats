package fr.hadrienmp.stats.tickets.source.jira

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import java.time.LocalDate

fun jiraTickets(credentials: Credentials, jiraHost: String, project: String, dateAfter: LocalDate) =
        JdkRequest("$jiraHost/rest/api/latest/search")
                .header("authorization", "Basic ${credentials.toBase64()}")
                .header("content-type", "application/json")
                .method(Request.POST)
                .body()
                .set("""
                    {
                        "jql": "project = $project AND created >= $dateAfter ORDER BY created DESC"
                    }
                """)
                .back()
                .fetch()

