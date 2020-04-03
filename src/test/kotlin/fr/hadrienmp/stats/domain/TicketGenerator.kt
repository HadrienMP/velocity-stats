package fr.hadrienmp.stats.domain

import java.time.LocalDate
import java.time.LocalDate.now

fun aTicket(type: TicketType = TicketType.FEATURE,
            finishDate: LocalDate? = null,
            createDate: LocalDate = now()): Ticket {
    return Ticket(
            createDate = createDate,
            type = type,
            points = 0,
            finishDate = finishDate ?: now()
    )
}

