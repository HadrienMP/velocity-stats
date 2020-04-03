package fr.hadrienmp.stats.domain

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate.now
import java.time.LocalDate.of
import java.time.Period

class TicketSpec: StringSpec({
    "the finished month of a ticket should return the first day of the month" {
        val finishedDate = of(2018,2,3)
        val ticket = aTicket(finishDate = finishedDate)
        ticket.finishMonth() shouldBe of(2018,2,1)
    }
    "the finished month of an unfinished ticket should be null" {
        val ticket = aTicket(finishDate = null)
        ticket.finishMonth() shouldBe null
    }
    "the cycle time of a ticket should be the time between the creation of the ticket and the finish date" {
        val ticket = aTicket(finishDate = now(), createDate = now().minusDays(1))
        ticket.cycleTime() shouldBe Period.ofDays(1)
    }
    "the cycle time of an unfinished ticket should be null" {
        val ticket = aTicket(finishDate = null, createDate = now().minusDays(1))
        ticket.cycleTime() shouldBe null
    }
})