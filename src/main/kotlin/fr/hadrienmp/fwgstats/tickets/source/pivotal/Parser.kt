package fr.hadrienmp.fwgstats.tickets.source.pivotal

import com.beust.klaxon.Klaxon
import fr.hadrienmp.lib.web.ZonedDateTimeConverter

object Parser {
    val parser = Klaxon().converter(ZonedDateTimeConverter)

    inline fun <reified T> parse(jsonResponse: String): T? {
        return parser.parse<T>(jsonResponse)
    }
}