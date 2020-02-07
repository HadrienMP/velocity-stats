package fr.hadrienmp.stats.tickets.source

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.*
import fr.hadrienmp.stats.domain.Tickets
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate.*

class TicketsCacheSpec: StringSpec({
    "Delegates to a tickets source" {
        val tickets = mock(Tickets::class.java)
        val ticketsCache = TicketsCache(tickets)
        val expected = listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        given(tickets.all()).willReturn(expected)

        val actual = ticketsCache.cachedAll()

        assertThat(actual).isEqualTo(expected);
    }

    "test test test" {
        val tickets = mock(Tickets::class.java)
        val ticketsCache = TicketsCache(tickets)
        given(tickets.all()).willReturn(listOf(Ticket(now(), FEATURE), Ticket(now(), BUG)))

        ticketsCache.cachedAll()
        ticketsCache.cachedAll()

        verify(tickets, times(1)).all()
    }
})

class TicketsCache(val tickets: Tickets){
    fun cachedAll() = tickets.all()
}