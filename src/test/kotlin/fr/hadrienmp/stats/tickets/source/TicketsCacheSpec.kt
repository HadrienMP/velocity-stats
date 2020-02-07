package fr.hadrienmp.stats.tickets.source

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.BUG
import fr.hadrienmp.stats.domain.TicketType.FEATURE
import fr.hadrienmp.stats.domain.Tickets
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import java.time.Duration
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.ZonedDateTime.parse

class TicketsCacheSpec : StringSpec({
    val date = parse("2018-12-31T00:00:00Z")
    "Delegates to a tickets source" {

        val expected = listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        val tickets = mock<Tickets> {
            on { after(any()) } doReturn expected
        }

        val ticketsCache = TicketsCache(tickets, Duration.ofMillis(100))

        val actual = ticketsCache.after(date)

        assertThat(actual).isEqualTo(expected)
    }

    "tickets are cached" {
        val tickets = mock<Tickets> {
            on { after(any()) } doReturn listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        }
        val ticketsCache = TicketsCache(tickets, Duration.ofMillis(100))

        ticketsCache.after(date)
        ticketsCache.after(date)

        verify(tickets, times(1)).after(date)
    }

    "call the source after expiration" {
        val tickets = mock<Tickets> {
            on { after(any()) } doReturn listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        }
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketsCache(tickets, duration)

        ticketsCache.after(date)
        Thread.sleep(duration.toMillis())
        ticketsCache.after(date)
        ticketsCache.after(date)

        verify(tickets, times(2)).after(date)
    }

    "tickets are refreshed after expiration" {
        val expected = listOf(Ticket(now(), FEATURE))
        val tickets = mock<Tickets> {
            on { after(any()) }.doReturn(listOf(Ticket(now(), FEATURE), Ticket(now(), BUG)), expected)
        }
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketsCache(tickets, duration)

        ticketsCache.after(date)
        Thread.sleep(duration.toMillis())
        val actual = ticketsCache.after(date)

        assertThat(actual).isEqualTo(expected)
    }
})

class TicketsCache(private val tickets: Tickets, private val timeToLive: Duration) : Tickets {
    private var cachedTickets = listOf<Ticket>()
    private var lastUpdate = LocalDateTime.now().minus(timeToLive).minusMinutes(1)

    override fun after(date: ZonedDateTime): List<Ticket> {
        if (isExpired())
            updateCache(date)
        return cachedTickets
    }

    private fun isExpired() = Duration.between(lastUpdate, LocalDateTime.now()) >= timeToLive

    private fun updateCache(date: ZonedDateTime) {
        lastUpdate = LocalDateTime.now()
        cachedTickets = tickets.after(date)
    }
}