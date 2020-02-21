package fr.hadrienmp.stats.tickets.source.jira

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*

fun jiraTickets(credentials: Credentials, jiraHost: String, project: String, dateAfter: LocalDate) =
        JdkRequest("$jiraHost/rest/api/latest/search")
                .header("authorization", "Basic ${credentials.toBase64()}")
                .header("content-type", "application/json")
                .method(Request.POST)
                .body()
                .set("""
                    {
                        "jql": "project = $project AND created >= $dateAfter ORDER BY created DESC"
                    }
                """)
                .back()
                .fetch()

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