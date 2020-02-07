package fr.hadrienmp.fwgstats.tickets.source.pivotal.model

data class PivotalTicketsResponse(val data: List<PivotalTicket>) {
    fun thereAreTicketsLeft() = data.isNotEmpty()
    fun tickets() = data
}

