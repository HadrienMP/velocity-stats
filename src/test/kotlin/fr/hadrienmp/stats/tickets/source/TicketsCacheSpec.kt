package fr.hadrienmp.stats.tickets.source

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.*
import fr.hadrienmp.stats.domain.Tickets
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.Duration
import java.time.LocalDate.*
import java.time.LocalDateTime
import java.time.ZonedDateTime

class TicketsCacheSpec: StringSpec({
    "Delegates to a tickets source" {
        val tickets = mock(Tickets::class.java)
        val expected = listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        given(tickets.all()).willReturn(expected)
        val ticketsCache = TicketsCache(tickets, Duration.ofMillis(100))

        val actual = ticketsCache.all()

        assertThat(actual).isEqualTo(expected)
    }

    "tickets are cached" {
        val tickets = mock(Tickets::class.java)
        given(tickets.all()).willReturn(listOf(Ticket(now(), FEATURE), Ticket(now(), BUG)))
        val ticketsCache = TicketsCache(tickets, Duration.ofMillis(100))

        ticketsCache.all()
        ticketsCache.all()

        verify(tickets, times(1)).all()
    }

    "tickets are cached 2" {
        val tickets = mock(Tickets::class.java)
        given(tickets.all()).willReturn(listOf(Ticket(now(), FEATURE), Ticket(now(), BUG)))
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketsCache(tickets, duration)

        ticketsCache.all()
        Thread.sleep(duration.toMillis())
        ticketsCache.all()

        verify(tickets, times(2)).all()
    }
})

class TicketsCache(private val tickets: Tickets, private val duration: Duration):Tickets{
    private val list = tickets.all()
    private val lastUpdate = LocalDateTime.now()

    override fun all(): List<Ticket> {
        if (isExpired())
            tickets.all()
        return list
    }

    private fun isExpired() = Duration.between(lastUpdate, LocalDateTime.now()) >= duration

    override fun after(date: ZonedDateTime): List<Ticket> {
        TODO("not implemented")
    }
}