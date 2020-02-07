package fr.hadrienmp.fwgstats.tickets.source.pivotal.client

import fr.hadrienmp.fwgstats.tickets.source.pivotal.model.PivotalTicket
import fr.hadrienmp.fwgstats.tickets.source.pivotal.model.PivotalTicketsResponse
import java.time.ZonedDateTime

class PivotalClient(projectId: Int, token: String) {
    private val pivotalPages = PivotalPageClient(token, projectId)

    fun tickets(createdAfter: ZonedDateTime? = null): List<PivotalTicket> {
        return generateSequence(0) { it + 100 }
                .map { offset -> pivotalPages.page(offset, createdAfter) }
                .takeWhile(PivotalTicketsResponse::thereAreTicketsLeft)
                .map(PivotalTicketsResponse::tickets)
                .fold(emptyList()) { a, b -> a + b }
    }

}

fun pivotalClientFrom(args: Array<String>): PivotalClient {
    return PivotalClient(pivotalProjectIdFrom(args), pivotalTokenFrom(args))
}

fun pivotalTokenFrom(args: Array<String>): String = args.toList()
        .filter { it.matches(Regex("pivotalToken=.+")) }
        .map { it.split("=").last() }
        .first()

fun pivotalProjectIdFrom(args: Array<String>): Int {
    return args.toList()
            .filter { it.matches(Regex("pivotalProjectId=\\d+")) }
            .map { it.split("=").last().toInt() }
            .first()
}