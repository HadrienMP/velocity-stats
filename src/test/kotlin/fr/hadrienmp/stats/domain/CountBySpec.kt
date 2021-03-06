package fr.hadrienmp.stats.domain

import io.kotlintest.matchers.containAll
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate
import java.time.LocalDate.now

class CountBySpec : StringSpec({
    val now = now()
    val lastMonth = now.minusMonths(1)

    "should return a map of keys the result of the given function, and values the number of elements matching the key" {
        val tickets = listOf(
                aTicket(finishDate = now, createDate = now()),
                aTicket(finishDate = now, createDate = now()),
                aTicket(finishDate = lastMonth, createDate = now()))

        val numbers = tickets.countBy(DoneTicket::finishMonth)

        numbers should containAll(mapOf<LocalDate, Int>(
                Pair(now.withDayOfMonth(1), 2),
                Pair(lastMonth.withDayOfMonth(1), 1)
        ))
    }
    "should not countBy elements for which the function returns null" {
        val tickets = listOf(aTicket(finishDate = null, createDate = now()))

        val numbers = tickets.countBy(DoneTicket::finishMonth)

        numbers.isEmpty() shouldBe true
    }
    "should return an empty map for an empty list" {
        val tickets = listOf<DoneTicket>()

        val numbers = tickets.countBy(DoneTicket::finishMonth)

        numbers.isEmpty() shouldBe true
    }
})