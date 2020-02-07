package fr.hadrienmp.stats.tickets.source

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.BUG
import fr.hadrienmp.stats.domain.TicketType.FEATURE
import fr.hadrienmp.stats.domain.Tickets
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.time.Duration
import java.time.LocalDate.now
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

    "tickets are cached by zoned date time" {
        val tickets = mock<Tickets> {}
        val ticketsCache = TicketsCache(tickets, Duration.ofMillis(100))
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
        val tickets = mock<Tickets> {
            on { after(any()) } doReturn listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        }
        val duration = Duration.ofMillis(100)
        val ticketsCache = TicketsCache(tickets, duration)

        ticketsCache.after(date)
        Thread.sleep(duration.toMillis())
        ticketsCache.after(date)
        ticketsCache.after(date)

        verify(tickets, times(2)).after(any())
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

class TicketsCache(private val tickets: Tickets, timeToLive: Duration) : Tickets {
    var cache: LoadingCache<ZonedDateTime, List<Ticket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build<ZonedDateTime, List<Ticket>> { key: ZonedDateTime -> tickets.after(key) }

    override fun after(date: ZonedDateTime) = cache.get(date) ?: tickets.after(date)
}