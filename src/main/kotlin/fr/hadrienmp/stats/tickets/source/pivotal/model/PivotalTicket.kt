package fr.hadrienmp.stats.tickets.source.pivotal.model

import com.beust.klaxon.Json
import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.TicketType
import fr.hadrienmp.stats.domain.TicketType.*
import java.time.ZonedDateTime

data class PivotalTicket(
        @Json(name = "created_at")
        val createdAt: ZonedDateTime,
        @Json(name = "updated_at")
        val updatedAt: ZonedDateTime,
        @Json(name = "story_type")
        val type: String,
        @Json(name = "accepted_at")
        val acceptedAt: ZonedDateTime,
        @Json(name = "estimate")
        private val estimate: Int? = null,
        @Json(name = "transitions")
        val transitionsInReverseOrder: List<Transition> = emptyList()
) {
    fun toTicket(): DoneTicket {
        return DoneTicket(
                createDate = createdAt.toLocalDate(),
                type = ticketType(),
                finishDate = acceptedAt.toLocalDate(),
                points = estimate
        )
    }

    private fun ticketType(): TicketType = when(type) {
        "feature" -> FEATURE
        "bug" -> BUG
        else -> UNKNOWN
    }

}
