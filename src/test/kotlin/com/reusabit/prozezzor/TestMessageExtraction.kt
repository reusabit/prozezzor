package com.reusabit.prozezzor

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader

private val MESSAGE = """
  14:19:33	 From  First Last : F22 Company Corp
  14:20:19	 From  First Last : First Last
  1234 Something Somewhere Lane NW #222
  Albuquerque, NM 88888
  (505) 123-1234
  """.trimIndent()

class TestMessageExtraction {
  @Test
  fun testExtractMessages() {
    val messages = extractMessages(BufferedReader(StringReader(MESSAGE)))

    assertThat(messages.size).isEqualTo(2)
    assertThat(messages[0]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "14:19:33",
          fromName = "First Last"
        ),
        linesRaw = listOf("14:19:33\t From  First Last : F22 Company Corp"),
        lines = listOf("F22 Company Corp"),
      )
    )
    assertThat(messages[1]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "14:20:19",
          fromName = "First Last"
        ),
        linesRaw = listOf(
          "14:20:19\t From  First Last : First Last",
          "1234 Something Somewhere Lane NW #222",
          "Albuquerque, NM 88888",
          "(505) 123-1234",
        ),
        lines = listOf(
          "First Last",
          "1234 Something Somewhere Lane NW #222",
          "Albuquerque, NM 88888",
          "(505) 123-1234",
        ),
        phone = listOf(
          ChatMessage.PhoneNumber("(505) 123-1234")
        )
      ),
    )
  }

}