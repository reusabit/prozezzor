package com.reusabit.prozezzor

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader

//This message has tab characters after timestamps.
private val MESSAGE = """
  14:19:33	 From  First Last : F22 Company Corp
  14:20:19	 From  First Last : First Last
  1234 Something Somewhere Lane NW #222
  Albuquerque, NM 88888
  (505) 123-1234
  """.trimIndent()


class TestMessageExtraction {
  @Test
  fun testNoToPortion() {
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

  @Test
  fun privateMessage() {
    //It looks like zoom stopped using a tab character after the timestamp.
    //Patterns have been updated to make this optional.
    //This message does not have a tab.
    val PRIVATE_MESSAGE = """
      15:14:56 From  John Doe  to  Everyone : Johny Doe
      Software Developer
      Awesome Software Corp
      Email: john@example.com
      Cell: (555) 123-5432
      Web: https://example.com
      LinkedIn: https://www.linkedin.com/in/john-doe/
      """.trimIndent()

    val messages = extractMessages(BufferedReader(StringReader(PRIVATE_MESSAGE)))
    assertThat(messages.size).isEqualTo(1)
    assertThat(messages[0]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "15:14:56",
          fromName = "John Doe",
          toName = "Everyone"
        ),
        linesRaw = listOf(
          "15:14:56 From  John Doe  to  Everyone : Johny Doe",
          "Software Developer",
          "Awesome Software Corp",
          "Email: john@example.com",
          "Cell: (555) 123-5432",
          "Web: https://example.com",
          "LinkedIn: https://www.linkedin.com/in/john-doe/",
        ),
        lines = listOf(
          "Johny Doe",
          "Software Developer",
          "Awesome Software Corp",
          "Email: john@example.com",
          "Cell: (555) 123-5432",
          "Web: https://example.com",
          "LinkedIn: https://www.linkedin.com/in/john-doe/",
        ),
        phone = listOf(ChatMessage.PhoneNumber("(555) 123-5432")),
        email = listOf(ChatMessage.Email("john@example.com")),
        url = listOf(
          ChatMessage.Url("https://example.com")
        ),
        linkedin = listOf(ChatMessage.Url("https://www.linkedin.com/in/john-doe/"))
      )
    )
  }

  @Test
  fun singleLineEmail() {
    val PRIVATE_MESSAGE = """
      15:14:05 From  Jane Doe  to  Everyone : jane.doe@gmail.com
      """.trimIndent()
    val messages = extractMessages(BufferedReader(StringReader(PRIVATE_MESSAGE)))
    assertThat(messages[0]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "15:14:05",
          fromName = "Jane Doe",
          toName = "Everyone"
        ),
        linesRaw = listOf("15:14:05 From  Jane Doe  to  Everyone : jane.doe@gmail.com"),
        lines = listOf("jane.doe@gmail.com"),
        email = listOf(ChatMessage.Email("jane.doe@gmail.com")),
        url = listOf(),
        linkedin = listOf()
      )
    )
  }

  //Mac format observed to leave out extra spaces and tabs.
  @Test
  fun macFormat() {
    // Contains tabs for the indented lines.
    val PRIVATE_MESSAGE = """
      17:16:02 From John Doe to Everyone:
      ${'\t'}555-555-5555
      17:16:07 From John Doe to Everyone:
      ${'\t'}johndoe@gmail.com
      """.trimIndent()

    val messages = extractMessages(BufferedReader(StringReader(PRIVATE_MESSAGE)))
    assertThat(messages.size).isEqualTo(2)
    assertThat(messages[0]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "17:16:02",
          fromName = "John Doe",
          toName = "Everyone"
        ),
        linesRaw = listOf(
          "17:16:02 From John Doe to Everyone:",
          "\t555-555-5555"
        ),
        lines = listOf(
          "", "\t555-555-5555"
        ),
        email = listOf(),
        phone = listOf(ChatMessage.PhoneNumber("555-555-5555")),
        url = listOf(),
        linkedin = listOf()
      )
    )
    assertThat(messages[1]).isEqualTo(
      ChatMessage(
        header = ChatMessage.Header(
          time = "17:16:07",
          fromName = "John Doe",
          toName = "Everyone"
        ),
        linesRaw = listOf(
          "17:16:07 From John Doe to Everyone:",
          "\tjohndoe@gmail.com"
        ),
        lines = listOf(
          "",
          "\tjohndoe@gmail.com"
        ),
        email = listOf(ChatMessage.Email("johndoe@gmail.com")),
        phone = listOf(),
        url = listOf(),
        linkedin = listOf()
      )
    )
  }
}
