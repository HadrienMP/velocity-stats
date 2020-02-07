package fr.hadrienmp.stats.tickets.source

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.*
import fr.hadrienmp.stats.domain.Tickets
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate.*
import java.time.ZonedDateTime

class TicketsCacheSpec: StringSpec({
    "Delegates to a tickets source" {
        val tickets = mock(Tickets::class.java)
        val expected = listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        given(tickets.all()).willReturn(expected)
        val ticketsCache = TicketsCache(tickets)

        val actual = ticketsCache.all()

        assertThat(actual).isEqualTo(expected)
    }

    "tickets are cached" {
        val tickets = mock(Tickets::class.java)
        given(tickets.all()).willReturn(listOf(Ticket(now(), FEATURE), Ticket(now(), BUG)))
        val ticketsCache = TicketsCache(tickets)

        ticketsCache.all()
        ticketsCache.all()

        verify(tickets, times(1)).all()
    }
})

class TicketsCache(tickets: Tickets):Tickets{
    private val list = tickets.all()

    override fun all() = list

    override fun after(date: ZonedDateTime): List<Ticket> {
        TODO("not implemented")
    }
}