package fr.hadrienmp.fwgstats.tickets.source.pivotal.model

import com.beust.klaxon.Json
import fr.hadrienmp.fwgstats.domain.Ticket
import fr.hadrienmp.fwgstats.domain.TicketType
import fr.hadrienmp.fwgstats.domain.TicketType.*
import java.time.LocalDate
import java.time.ZonedDateTime

data class PivotalTicket(
        @Json(name = "created_at")
        val createdAt: ZonedDateTime,
        @Json(name = "updated_at")
        val updatedAt: ZonedDateTime,
        @Json(name = "story_type")
        val type: String,
        @Json(name = "accepted_at")
        val acceptedAt: ZonedDateTime? = null,
        @Json(name = "estimate")
        private val estimate: Int? = null,
        @Json(name = "transitions")
        val transitionsInReverseOrder: List<Transition> = emptyList()
) {
    fun toTicket(): Ticket {
        return Ticket(
                createDate = createdAt.toLocalDate(),
                type = ticketType(),
                finishDate = acceptedAt?.toLocalDate(),
                points = estimate,
                prApprobationDate = lastDelivery(),
                devFinishDate = lastFinish(),
                devStartDate = firstStart()
        )
    }

    private fun ticketType(): TicketType = when(type) {
        "feature" -> FEATURE
        "bug" -> BUG
        else -> UNKNOWN
    }

    private fun lastFinish() = last("finished")

    private fun lastDelivery() = last("delivered")

    private fun last(expectedState: String): LocalDate? {
        if (acceptedAt == null) return null
        return transitionsInReverseOrder
                .firstOrNull { it.state == expectedState }
                ?.let { it.date.toLocalDate() }
    }

    private fun firstStart(): LocalDate? {
        return transitionsInReverseOrder
                .lastOrNull {it.state == "started"}
                ?.let { it.date.toLocalDate() }
    }
}
