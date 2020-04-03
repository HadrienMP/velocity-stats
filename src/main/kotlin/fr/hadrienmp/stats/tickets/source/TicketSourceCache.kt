package fr.hadrienmp.stats.tickets.source

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.TicketSource
import java.time.Duration
import java.time.ZonedDateTime

class TicketSourceCache(timeToLive: Duration, private val ticketSources: List<TicketSource>) : TicketSource {
    var cache: LoadingCache<ZonedDateTime, List<DoneTicket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build { key: ZonedDateTime -> ticketSources.flatMap { it.doneTicketsAfter(key) } }

    override fun doneTicketsAfter(analysisStartDate: ZonedDateTime) = cache.get(analysisStartDate)
            ?: ticketSources.flatMap { it.doneTicketsAfter(analysisStartDate) }
}