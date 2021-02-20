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

  @Test
  fun testEmailAndUrl() {
    val message = """
      14:02:23	 From  First Last - Some Cool Bis : ---------------------------
      First Last - Some Cool Bis: Technical Tech Geeks
      Somewhere, Delaware USA
      https://www.somecoolbis.com
      https://www.realycoolbis.com

      First Last, MD, JD, CPA
      https://www.linkedin.com/in/flast
      first@somecoolbisemail.com
      555-555-5555

      I have an awesome book, get it from Amazon:
      Super Cool Book
      https://www.amazon.com/Super-Cool-Book/dp/ABCDEF1234

      What can I do for you?
      ---------------------------
      """.trimIndent()

    val messages = extractMessages(BufferedReader(StringReader(message)))

    assertThat(messages.size).isEqualTo(1)
    messages[0].let{m->
      assertThat(m.email).contains(ChatMessage.Email(raw="first@somecoolbisemail.com"))
      assertThat(m.phone).contains(ChatMessage.PhoneNumber(raw="555-555-5555"))
      assertThat(m.url).doesNotContain(ChatMessage.Url(raw = "somecoolbisemail.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.somecoolbis.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.realycoolbis.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.amazon.com/Super-Cool-Book/dp/ABCDEF1234"))
      assertThat(m.linkedin).contains(ChatMessage.Url(raw = "https://www.linkedin.com/in/flast"))
    }
  }

  @Test
  fun testEmailAndUrlReveseOrder() {
    val message = """
      14:02:23	 From  First Last - Some Cool Bis : ---------------------------
      First Last - Some Cool Bis: Technical Tech Geeks

      First Last, MD, JD, CPA
      first@somecoolbisemail.com
      https://www.linkedin.com/in/flast
      555-555-5555

      Somewhere, New Mexico USA
      https://www.somecoolbis.com
      https://www.realycoolbis.com

      I have an awesome book, get it from Amazon:
      Super Cool Book
      https://www.amazon.com/Super-Cool-Book/dp/ABCDEF1234

      What can I do for you?
      ---------------------------
      """.trimIndent()

    val messages = extractMessages(BufferedReader(StringReader(message)))

    assertThat(messages.size).isEqualTo(1)
    messages[0].let{m->
      assertThat(m.email).contains(ChatMessage.Email(raw="first@somecoolbisemail.com"))
      assertThat(m.phone).contains(ChatMessage.PhoneNumber(raw="555-555-5555"))
      assertThat(m.url).doesNotContain(ChatMessage.Url(raw = "somecoolbisemail.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.somecoolbis.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.realycoolbis.com"))
      assertThat(m.url).contains(ChatMessage.Url(raw = "https://www.amazon.com/Super-Cool-Book/dp/ABCDEF1234"))
      assertThat(m.linkedin).contains(ChatMessage.Url(raw = "https://www.linkedin.com/in/flast"))
    }
  }
}