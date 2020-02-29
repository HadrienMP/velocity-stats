package fr.hadrienmp.stats.tickets.source

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.BUG
import fr.hadrienmp.stats.domain.TicketType.FEATURE
import fr.hadrienmp.stats.domain.TicketSource
import fr.hadrienmp.stats.domain.aTicket
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.Duration
import java.time.LocalDate.now
import java.time.ZonedDateTime.parse


class TicketSourceCacheSpec : StringSpec({
    val date = parse("2018-12-31T00:00:00Z")

    "Delegates to a tickets source" {

        val expected = listOf(aTicket())
        val tickets = mock<TicketSource> {
            on { after(any()) } doReturn expected
        }

        val ticketsCache = TicketSourceCache(Duration.ofMillis(100), listOf(tickets))

        val actual = ticketsCache.after(date)

        assertThat(actual).isEqualTo(expected)
    }

    "tickets are cached by zoned date time" {
        val tickets = mock<TicketSource> {}
        val ticketsCache = TicketSourceCache(Duration.ofMillis(100), listOf(tickets))
        val firstDate = parse("2010-01-01T00:00:00Z")
        val secondDate = parse("2019-12-31T00:00:00Z")

        ticketsCache.after(firstDate)
        ticketsCache.after(secondDate)
        ticketsCache.after(firstDate)
        ticketsCache.after(secondDate)

        verify(tickets, times(1)).after(firstDate)
        verify(tickets, times(1)).after(secondDate)
    }

    "call the source after expiration" {
        val tickets = mock<TicketSource> {
            on { after(any()) } doReturn listOf(aTicket(type = FEATURE), aTicket(type = BUG))
        }
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketSourceCache(duration, listOf(tickets))

        ticketsCache.after(date)
        Thread.sleep(duration.toMillis())
        ticketsCache.after(date)
        ticketsCache.after(date)

        verify(tickets, times(2)).after(any())
    }

    "tickets are refreshed after expiration" {
        val expected = listOf(Ticket(now(), FEATURE))
        val tickets = mock<TicketSource> {
            on { after(any()) }.doReturn(listOf(aTicket(type = FEATURE), aTicket(type = BUG)), expected)
        }
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketSourceCache(duration, listOf(tickets))

        ticketsCache.after(date)
        Thread.sleep(duration.toMillis())
        val actual = ticketsCache.after(date)

        assertThat(actual).isEqualTo(expected)
    }
})

