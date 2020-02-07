package fr.hadrienmp.stats.tickets.source

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketType
import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicket
import fr.hadrienmp.stats.tickets.source.pivotal.model.Transition
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZonedDateTime

class TicketsCacheSpec {

    @Test
    internal fun name() {
        assertThat(listOf(Ticket(LocalDate.now(), TicketType.FEATURE),Ticket(LocalDate.now(), TicketType.BUG))).isEqualTo(listOf(Ticket(LocalDate.now(), TicketType.FEATURE),Ticket(LocalDate.now(), TicketType.BUG)));
    }
}