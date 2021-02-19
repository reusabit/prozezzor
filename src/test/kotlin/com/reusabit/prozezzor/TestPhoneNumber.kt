package com.reusabit.prozezzor

import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test

class TestPhoneNumber {
  @Test
  fun testPhoneNumber() {
    val pn = "(505) 123-1234"
    val results = ChatMessage.PhoneNumber.findAllIn(pn)

    Assertions.assertThat(results.size).isEqualTo(1)
    Assertions.assertThat(results[0]).isEqualTo(ChatMessage.PhoneNumber(raw=pn))
  }

  @Ignore
  @Test
  fun regexWordBoundarySanityCheck() {
    val s = "1"
    val pattern = """\b1\b"""
    val regex = Regex(pattern)
    Assertions.assertThat(regex.matches(s)).isTrue
  }

  @Ignore
  @Test
  fun regexParenSanityCheck() {
    val s = "(1)"
    val pattern = """[(]1[)]"""
    val regex = Regex(pattern)
    Assertions.assertThat(regex.matches(s)).isTrue
  }

  @Ignore
  @Test
  fun regexParenBoundarySanityCheck() {
    val s = "(1)"
    val pattern = """\b[(]1[)]\b"""
    val regex = Regex(pattern)
    Assertions.assertThat(regex.matches(s)).isFalse //.isTrue //Ah hah: No word boundary between paren and nothing.
  }

}