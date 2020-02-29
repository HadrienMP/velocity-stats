package fr.hadrienmp.stats.api.web

import fr.hadrienmp.lib.web.AppArguments
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.*

class JiraFromCliSpec: StringSpec({
    "returns null when the jira arguments are missing" {
        assertThat(jiraPageClientFrom(AppArguments(emptyArray()))).isNull()
    }
})