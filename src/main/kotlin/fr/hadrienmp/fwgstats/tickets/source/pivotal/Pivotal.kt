package fr.hadrienmp.fwgstats.tickets.source.pivotal

import fr.hadrienmp.fwgstats.domain.Ticket
import fr.hadrienmp.fwgstats.domain.Tickets
import fr.hadrienmp.fwgstats.tickets.source.pivotal.client.PivotalClient
import fr.hadrienmp.fwgstats.tickets.source.pivotal.model.PivotalTicket
import java.time.ZonedDateTime

class Pivotal(private val pivotalClient: PivotalClient) : Tickets {
    override fun all(): List<Ticket> {
        return pivotalClient.tickets().map(PivotalTicket::toTicket)
    }

    override fun after(date: ZonedDateTime): List<Ticket> {
        return pivotalClient.tickets(date).map(PivotalTicket::toTicket)
    }
}