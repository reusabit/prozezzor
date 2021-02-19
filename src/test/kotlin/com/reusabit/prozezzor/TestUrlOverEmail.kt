package com.reusabit.prozezzor

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.BufferedReader
import java.io.StringReader

class TestUrlOverEmail {
  @Test
  fun testEmailNotUrl() {
    val message = """
      14:02:23	 From  First Last, Las Vegas : First Last - Best Business Helpers -first@mypage.com - 555-555-5555
      """.trimIndent()

    val messages = extractMessages(BufferedReader(StringReader(message)))

    assertThat(messages.size).isEqualTo(1)
    messages[0].let{m->
      assertThat(m.email).contains(ChatMessage.Email(raw="first@mypage.com"))
      assertThat(m.phone).contains(ChatMessage.PhoneNumber(raw="555-555-5555"))
      assertThat(m.url).doesNotContain(ChatMessage.Url(raw = "mypage.com"))
    }

  }
}