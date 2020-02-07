package fr.hadrienmp.stats.tickets.source.pivotal

import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicket
import fr.hadrienmp.stats.tickets.source.pivotal.model.Transition
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate
import java.time.ZonedDateTime

class PivotalTicketSpec : StringSpec({

    val zonedNow = ZonedDateTime.now()
    val localNow = LocalDate.now()


    "the dev finish date should be the date of the last finished transition" {
        val ticket = PivotalTicket(
                type = "feature",
                createdAt = zonedNow,
                updatedAt = zonedNow,
                acceptedAt = zonedNow,
                transitionsInReverseOrder = listOf(
                        Transition("accepted", zonedNow),
                        Transition("delivered", zonedNow.minusDays(1)),
                        Transition("finished", zonedNow.minusDays(2)),
                        Transition("started", zonedNow.minusDays(3)),
                        Transition("unstarted", zonedNow.minusDays(4)),
                        Transition("delivered", zonedNow.minusDays(5)),
                        Transition("finished", zonedNow.minusDays(6)),
                        Transition("started", zonedNow.minusDays(7)),
                        Transition("planned", zonedNow.minusDays(8))
                ))

        ticket.toTicket().prApprobationDate shouldBe localNow.minusDays(1)
    }

    "the dev finish date should be null when the ticket is not accepted" {
        val ticket = PivotalTicket(
                type = "feature",
                createdAt = zonedNow,
                updatedAt = zonedNow,
                acceptedAt = null,
                transitionsInReverseOrder = listOf(
                        Transition("delivered", zonedNow)
                ))

        ticket.toTicket().prApprobationDate shouldBe null
    }

    "the dev start date should be the date of the first start transition" {
        val ticket = PivotalTicket(
                type = "feature",
                createdAt = zonedNow,
                updatedAt = zonedNow,
                acceptedAt = zonedNow,
                transitionsInReverseOrder = listOf(
                        Transition("accepted", zonedNow),
                        Transition("delivered", zonedNow.minusDays(1)),
                        Transition("finished", zonedNow.minusDays(2)),
                        Transition("started", zonedNow.minusDays(3)),
                        Transition("unstarted", zonedNow.minusDays(4)),
                        Transition("delivered", zonedNow.minusDays(5)),
                        Transition("finished", zonedNow.minusDays(6)),
                        Transition("started", zonedNow.minusDays(7)),
                        Transition("planned", zonedNow.minusDays(8))
                ))

        ticket.toTicket().prApprobationDate shouldBe localNow.minusDays(1)
    }

    "the dev start date should be null when there is not started transition" {
        val ticket = PivotalTicket(
                type = "feature",
                createdAt = zonedNow,
                updatedAt = zonedNow,
                transitionsInReverseOrder = listOf())

        ticket.toTicket().prApprobationDate shouldBe null
    }
})