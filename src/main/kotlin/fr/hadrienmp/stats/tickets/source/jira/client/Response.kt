package fr.hadrienmp.stats.tickets.source.jira.client

import com.beust.klaxon.Json
import fr.hadrienmp.stats.domain.TicketType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Response(val startAt: Int,
                    val maxResults: Int,
                    val total: Int,
                    val issues: List<Ticket>)

data class Ticket(val key: String, val fields: Fields) {
    fun toCore(): fr.hadrienmp.stats.domain.DoneTicket {
        return fr.hadrienmp.stats.domain.DoneTicket(
                createDate = parseJiraDate(fields.created),
                points = fields.estimate?.toInt(),
                type = when (fields.type.name) {
                    "Récit" -> TicketType.FEATURE
                    "Bogue" -> TicketType.BUG
                    else -> TicketType.UNKNOWN
                },
                finishDate = parseJiraDate(fields.acceptedAt)
        )
    }

    private fun parseJiraDate(it: String) =
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
}
data class Fields(val status: Status? = null,
                  val created: String,
                  @Json(name = "resolutiondate")
                  val acceptedAt: String,
                  @Json(name = "customfield_10002")
                  val estimate: Float? = null,
                  @Json(name = "issuetype")
                  val type: IssueType,
                  val fixVersions: List<FixVersion> = emptyList())

data class FixVersion(val name: String)
data class IssueType(val name: String)
data class Status(val statusCategory: StatusCategory)
data class StatusCategory(val key: String)