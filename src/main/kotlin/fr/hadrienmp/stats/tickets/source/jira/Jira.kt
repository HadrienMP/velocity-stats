package fr.hadrienmp.stats.tickets.source.jira

import com.jcabi.http.Request
import com.jcabi.http.request.JdkRequest
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.*

fun jiraTickets(credentials: Credentials, project: String, dateAfter: LocalDate, jiraHost: String) =
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

class Credentials(private val userName: String, private val password: String) {
    fun toBase64(): String {
        val bytesCredentials = "$userName:$password".toByteArray(Charset.defaultCharset())
        return Base64.getEncoder().encodeToString(bytesCredentials)!!
    }

}