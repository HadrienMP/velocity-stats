package fr.hadrienmp.stats.domain

import java.time.LocalDate
import java.time.LocalDate.now

fun aTicket(type: TicketType = TicketType.FEATURE,
            finishedOn: LocalDate? = null,
            createdOn: LocalDate = now()): Ticket {
    return Ticket(
            createDate = createdOn,
            type = type,
            points = 0,
            finishDate = finishedOn
    )
}

