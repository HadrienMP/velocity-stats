package fr.hadrienmp.stats.tickets.source.jira

import com.beust.klaxon.Json

data class Response(val startAt: Int,
                    val maxResults: Int,
                    val total: Int,
                    val issues: List<Ticket>)

data class Ticket(val fields: Fields)
data class Fields(val status: Status? = null,
                  val created: String,
                  @Json(name = "resolutiondate")
                  val acceptedAt: String? = null,
                  @Json(name = "customfield_10002")
                  val estimate: Float = 0f,
                  @Json(name = "issuetype")
                  val type: IssueType)

data class IssueType(val name: String)
data class Status(val statusCategory: StatusCategory)
data class StatusCategory(val key: String)