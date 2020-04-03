package fr.hadrienmp.stats.tickets.source.pivotal

import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.FutureTicket
import fr.hadrienmp.stats.domain.TicketSource
import fr.hadrienmp.stats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicket
import java.time.ZonedDateTime

class Pivotal(private val pivotalClient: PivotalClient) : TicketSource {
    override fun doneTicketsAfter(analysisStartDate: ZonedDateTime): List<DoneTicket> {
        return pivotalClient.tickets(analysisStartDate).map(PivotalTicket::toTicket)
    }

    override fun future(): List<FutureTicket> = emptyList()
}