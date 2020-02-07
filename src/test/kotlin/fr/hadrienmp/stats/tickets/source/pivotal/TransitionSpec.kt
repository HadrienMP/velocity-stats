package fr.hadrienmp.stats.tickets.source.pivotal

import fr.hadrienmp.stats.tickets.source.pivotal.model.Transition
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.assertj.core.api.Assertions.assertThat
import java.time.ZonedDateTime.now


class TransitionSpec : StringSpec({

    val now = now()

    "should be deserializable from pivotal transition" {
        val jsonTransition = """
            {
              "state": "accepted",
              "story_id": 155583051,
              "project_id": 2019903,
              "project_version": 6426,
              "occurred_at": "2018-02-28T13:52:09Z",
              "performed_by_id": 56943,
              "kind": "story_transition"
            }
        """.trimIndent()

        val transition = Parser.parse<Transition>(jsonTransition)!!

        transition.state shouldBe "accepted"
        transition.date.toString() shouldBe "2018-02-28T13:52:09Z"
    }

    "should be sorted by date" {
        val sorted = listOf(
                Transition("delivered", now.minusDays(1)),
                Transition("accepted", now)
        )
        val scrambled = listOf(
                Transition("accepted", now),
                Transition("delivered", now.minusDays(1))
        )
        assertThat(scrambled.sorted()).isEqualTo(sorted)
    }
})

