package fr.hadrienmp.stats.domain

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate.now

class FeaturesFinishedByMonthSpec : StringSpec({
    "should countBy features by finishMonth" {
        val first = aTicket(finishedOn = now())
        val second = aTicket(finishedOn = now().minusMonths(1))
        val tickets = listOf(first, first, second)

        val count = featuresFinishedByMonth(tickets)

        count shouldBe mapOf(
                Pair(first.finishMonth()!!, 2),
                Pair(second.finishMonth()!!, 1)
        )
    }
    "should not countBy other type of tickets" {
        val tickets = listOf(aTicket(type = TicketType.UNKNOWN, finishedOn = now()))

        val count = featuresFinishedByMonth(tickets)

        count shouldBe emptyMap()
    }
    "should not countBy unfinished features" {
        val tickets = listOf(aTicket())

        val count = featuresFinishedByMonth(tickets)

        count shouldBe emptyMap()
    }
})

