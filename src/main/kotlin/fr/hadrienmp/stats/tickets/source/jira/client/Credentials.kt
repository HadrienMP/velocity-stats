package fr.hadrienmp.stats.tickets.source.jira.client

import fr.hadrienmp.lib.web.AppArguments
import java.nio.charset.Charset
import java.util.*

class Credentials(private val userName: String, private val password: String) {
    companion object {
        fun from(args: AppArguments): Credentials? {
            val userName = args.find("jira.user") ?: return null
            val password = args.find("jira.password") ?: return null
            return Credentials(userName, password)
        }
    }

    fun toBase64(): String {
        val bytesCredentials = "$userName:$password".toByteArray(Charset.defaultCharset())
        return Base64.getEncoder().encodeToString(bytesCredentials)!!
    }
}