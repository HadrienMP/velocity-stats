package fr.hadrienmp.stats.tickets.source.jira.client

import com.beust.klaxon.Json
import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.FutureTicket
import fr.hadrienmp.stats.domain.TicketType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Response(val startAt: Int,
                    val maxResults: Int,
                    val total: Int,
                    val issues: List<Ticket>)

data class Ticket(val key: String, val fields: Fields) {
    fun toCore() = DoneTicket(
            createDate = parseJiraDate(fields.created),
            points = fields.estimate?.toInt(),
            type = when (fields.type.name) {
                "Récit" -> when (fields.estimate) {
                    null -> TicketType.CHORE
                    0f -> TicketType.CHORE
                    else -> TicketType.FEATURE
                }
                "Bogue" -> TicketType.BUG
                "Chore" -> TicketType.CHORE
                else -> TicketType.UNKNOWN
            },
            finishDate = parseJiraDate(fields.acceptedAt!!)
    )

    private fun parseJiraDate(it: String) =
            LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))

    fun toFutureTicket() = FutureTicket(key, fields.title)
}

data class Fields(val status: Status? = null,
                  val created: String,
                  @Json(name = "resolutiondate")
                  val acceptedAt: String? = null,
                  @Json(name = "customfield_10002")
                  val estimate: Float? = null,
                  @Json(name = "issuetype")
                  val type: IssueType,
                  @Json(name = "summary")
                  val title: String,
                  val fixVersions: List<FixVersion> = emptyList())

data class FixVersion(val name: String)
data class IssueType(val name: String)
data class Status(val statusCategory: StatusCategory)
data class StatusCategory(val key: String)