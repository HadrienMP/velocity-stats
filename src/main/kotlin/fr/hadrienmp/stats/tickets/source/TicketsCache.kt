package fr.hadrienmp.stats.tickets.source

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import fr.hadrienmp.stats.domain.Ticket
import fr.hadrienmp.stats.domain.Tickets
import java.time.Duration
import java.time.ZonedDateTime

class TicketsCache(private val tickets: Tickets, timeToLive: Duration) : Tickets {
    var cache: LoadingCache<ZonedDateTime, List<Ticket>> = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(timeToLive)
            .build<ZonedDateTime, List<Ticket>> { key: ZonedDateTime -> tickets.after(key) }

    override fun after(analysisStartDate: ZonedDateTime) = cache.get(analysisStartDate) ?: tickets.after(analysisStartDate)
}