package fr.hadrienmp.stats.tickets.source

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.TicketSource
import java.time.Duration
import java.time.ZonedDateTime

class TicketSourceCache(timeToLive: Duration, private val ticketSources: Array<out TicketSource>) : TicketSource {
    var cache: LoadingCache<ZonedDateTime, List<Ticket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build<ZonedDateTime, List<Ticket>> { key: ZonedDateTime -> ticketSources.flatMap { it.after(key) } }

    override fun after(analysisStartDate: ZonedDateTime) = cache.get(analysisStartDate)
            ?: ticketSources.flatMap { it.after(analysisStartDate) }
}