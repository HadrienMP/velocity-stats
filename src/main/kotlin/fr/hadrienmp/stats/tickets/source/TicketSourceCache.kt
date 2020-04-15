package fr.hadrienmp.stats.tickets.source

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import fr.hadrienmp.stats.domain.DoneTicket
import fr.hadrienmp.stats.domain.FutureTicket
import fr.hadrienmp.stats.domain.TicketSource
import java.time.Duration
import java.time.ZonedDateTime

class TicketSourceCache(timeToLive: Duration, private val ticketSources: List<TicketSource>) : TicketSource {
    var doneTicketsCache: LoadingCache<ZonedDateTime, List<DoneTicket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build { key: ZonedDateTime -> ticketSources.flatMap { it.doneTicketsAfter(key) } }
    var futureTicketsCache: LoadingCache<String, List<FutureTicket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build { ticketSources.flatMap { it.future() } }

    override fun doneTicketsAfter(analysisStartDate: ZonedDateTime): List<DoneTicket> {
        return doneTicketsCache.get(analysisStartDate)
                ?: ticketSources.flatMap { it.doneTicketsAfter(analysisStartDate) }
    }

    override fun future(): List<FutureTicket> {
        return futureTicketsCache.get("all") ?: ticketSources.flatMap { it.future() }
    }
}