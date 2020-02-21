package fr.hadrienmp.stats.tickets.source.jira

import com.beust.klaxon.Json
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
            .fetch()!!

data class Response(val startAt: Int,
                    val maxResults: Int,
                    val total: Int,
                    val issues: List<Ticket>)

data class Ticket(val fields: Fields)

data class Fields(val status: Status? = null,
                  @Json(name = "resolutiondate")
                  val acceptedAt: String? = null,
                  @Json(name = "customfield_10002")
                  val estimate: Float = 0f,
                  @Json(name = "issuetype")
                  val type: IssueType)

data class IssueType(val name: String)

data class Status(val statusCategory: StatusCategory)

data class StatusCategory(val key: String)

