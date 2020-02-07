package fr.hadrienmp.stats.tickets.source

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType.*
import fr.hadrienmp.stats.domain.Tickets
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import java.time.LocalDate.*

class TicketsCacheSpec {

    @Test
    internal fun `Delegates to a tickets source`() {
        val tickets = mock(Tickets::class.java)
        val expected = listOf(Ticket(now(), FEATURE), Ticket(now(), BUG))
        given(tickets.all()).willReturn(expected)

        val actual = TicketsCache(tickets).cachedAll()

        assertThat(actual).isEqualTo(expected);
    }

    class TicketsCache(val tickets: Tickets){
        fun cachedAll() = tickets.all()
    }
}