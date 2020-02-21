package fr.hadrienmp.stats.tickets.source.jira

import java.nio.charset.Charset
import java.util.*

fun credentials(args: Array<String>): Credentials {
    val userName = args.first { it.matches(Regex("jira.user=.*")) }.split("=")[1]
    val password = args.first { it.matches(Regex("jira.password=.*")) }.split("=")[1]
    return Credentials(userName, password)
}

class Credentials(private val userName: String, private val password: String) {
    fun toBase64(): String {
        val bytesCredentials = "$userName:$password".toByteArray(Charset.defaultCharset())
        return Base64.getEncoder().encodeToString(bytesCredentials)!!
    }
}