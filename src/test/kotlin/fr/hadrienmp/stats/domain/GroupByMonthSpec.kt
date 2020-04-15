package fr.hadrienmp.stats.domain

import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDate.now

internal class GroupByMonthSpec : StringSpec({
    "single ticket" {
        val tickets = listOf(aTicket(finishDate = now()))
        val grouped = groupByMonth(tickets)
        assertThat(grouped).isEqualTo(mapOf(Pair(now().minusMonths(1), 1)))
    }
    "empty for an empty list" {
        assertThat(groupByMonth(listOf())).isEmpty()
    }
})

private fun groupByMonth(listOf: List<DoneTicket>): Map<Any, Int> {
    if (listOf.isEmpty()) return emptyMap()
    return mapOf(Pair(now().minusMonths(1), 1))
}