package fr.hadrienmp.stats.tickets.source.pivotal

import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.Tickets
import fr.hadrienmp.stats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.stats.tickets.source.pivotal.model.PivotalTicket
import java.time.ZonedDateTime

class Pivotal(private val pivotalClient: PivotalClient) : Tickets {
    override fun after(analysisStartDate: ZonedDateTime): List<Ticket> {
        return pivotalClient.tickets(analysisStartDate).map(PivotalTicket::toTicket)
    }
}