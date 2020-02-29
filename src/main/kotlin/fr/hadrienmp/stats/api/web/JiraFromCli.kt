package fr.hadrienmp.stats.api.web

import fr.hadrienmp.lib.web.AppArguments
import fr.hadrienmp.stats.tickets.source.jira.client.Credentials
import fr.hadrienmp.stats.tickets.source.jira.client.DefaultPageClient

fun jiraPageClientFrom(args: AppArguments): DefaultPageClient? {
    val project = args.find("jira.project") ?: return null
    val jiraHost = args.find("jira.host") ?: return null
    val credentials = Credentials.from(args) ?: return null
    return DefaultPageClient(credentials, jiraHost, project)
}

