package fr.hadrienmp.lib.web

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import java.time.ZonedDateTime

object ZonedDateTimeConverter: Converter<ZonedDateTime> {
    override fun fromJson(jv: JsonValue) = ZonedDateTime.parse(jv.string)

    override fun toJson(value: ZonedDateTime): String? {
        return value.toString()
    }

}