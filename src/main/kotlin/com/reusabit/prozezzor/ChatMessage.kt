/**
Copyright 2021 Reusabit Software LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.reusabit.prozezzor

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList

private val HOUR24_PATTERN = """(2[0123]|[01][0-9])"""
private val MINUTE_PATTERN = """([0-5][0-9])"""
private val SECOND_PATTERN = """(60|[0-5][0-9])""" //Supports leap seconds.
private val TIME_PATTERN = """$HOUR24_PATTERN:$MINUTE_PATTERN:$SECOND_PATTERN"""
private val HEADER_PATTERN = """$TIME_PATTERN\t From  (.*?) : (.*)"""
private val HEADER_PATTERN_DIRECT_MESSAGE = """$TIME_PATTERN\t From  (.*?)  to  (.*?)\(Direct message\) : (.*)"""
private val PHONE_PATTERN = """[(]?\b[0-9]{3}[ \t]*[-).]?[ \t]*[0-9]{3}[ \t]*[-.]?[0-9]{4}\b"""

private val DOMAIN_SERVER_PATTERN = """[-a-zA-Z0-9]+"""
private val DOMAIN_PATTERN = """${DOMAIN_SERVER_PATTERN}(\.${DOMAIN_SERVER_PATTERN})+\.?"""
private val PROBABLE_DOMAIN_PATTERN =
"""${DOMAIN_SERVER_PATTERN}(\.${DOMAIN_SERVER_PATTERN}){2,}\.?""" //Has at least three server names, eg. "www.example.com" rather than "example.com"
private val PROBABLE_DOMAIN_PATTERN2 = """(www\.)${DOMAIN_SERVER_PATTERN}(\.${DOMAIN_SERVER_PATTERN})+\.?"""
private val PROBABLE_DOMAIN_PATTERN3 =
"""${DOMAIN_SERVER_PATTERN}(\.${DOMAIN_SERVER_PATTERN})*\.(com|net|org|gov|mil|io|info|dev|de|icu|uk|ru|top|xyz|tk|cn|ga|cf|nl)\.?"""
private val PROTOCOL_PATTERN = """(http://|https://)"""

// This is simplified somewhat in order to pick up "common" urls. Businesses shouldn't be using big, ugly urls for contact information.
private val SUBDIRECTORY_PATTERN = """((/[-_+%&?a-zA-Z0-9]+)+)"""

private val DEFINITE_URL_PATTERN = """${PROTOCOL_PATTERN}${DOMAIN_PATTERN}${SUBDIRECTORY_PATTERN}?"""
private val PROBABLE_URL_PATTERN = """(?<!^|[^@])(${PROBABLE_DOMAIN_PATTERN}${SUBDIRECTORY_PATTERN}?)"""
private val PROBABLE_URL_PATTERN2 = """(?<!^|[^@])(${PROBABLE_DOMAIN_PATTERN2}${SUBDIRECTORY_PATTERN}?)"""
private val PROBABLE_URL_PATTERN3 = """(?<!^|[^@])(${PROBABLE_DOMAIN_PATTERN3}${SUBDIRECTORY_PATTERN}?)"""


//The email specification allows for complicated addresses.
//In my experience, it would be bad business to use something crazy as an email address.
//So this pattern is simplified in order to properly match "usual" business email addresses.
private val EMAIL_PATTERN = """[-._a-zA-Z0-9]+@${DOMAIN_PATTERN}"""


private val LINKEDIN_PATTERN = """${PROTOCOL_PATTERN}?(www\.)?linkedin.com/(.*)"""
private val LINKEDIN_REGEX = Regex(LINKEDIN_PATTERN)

interface MatchAgainst<R : Any> {
  fun matchAgainst(s: String): Pair<R?, String>
}

/**
 * Utility for matching the pattern against the beginning of input strings, returning a constructed result and
 * the remainder of the input string.
 *
 * Designed for matching heading lines.
 */
class MatchAgainstImpl<R : Any>(val patternString: String, val action: (matcher: Matcher) -> Pair<R, String>) :
  MatchAgainst<R> {
  val pattern = Pattern.compile(patternString)

  override fun matchAgainst(s: String): Pair<R?, String> {
    val matcher = pattern.matcher(s)
    if (matcher.matches()) {
      return action(matcher)
    } else return Pair(null, s)
  }
}

/**
 * Combine several possible patterns into a single MatchAgainst call. Starts with the first pattern, and returns
 * result from first successful match, or no result if none match.
 */
class MatchAgainstMultiImpl<R : Any>(val patternStrings: List<Pair<String, (matcher: Matcher) -> Pair<R, String>>>) :
  MatchAgainst<R> {
  val matchAgainsts = patternStrings.map { MatchAgainstImpl<R>(it.first, it.second) }

  override fun matchAgainst(s: String): Pair<R?, String> {
    matchAgainsts.forEach { matchAgainst ->
      val (result, remainder) = matchAgainst.matchAgainst(s)
      if (result != null) return Pair(result, remainder)
    }
    return Pair(null, s)
  }
}

interface FindAllIn<R : Any> {
  fun findAllIn(s: String): List<R>
}

class FindAllInImpl<R : Any>(val patternString: String, val action: (matcher: Matcher) -> R) : FindAllIn<R> {
  val pattern = Pattern.compile(patternString)

  override fun findAllIn(s: String): List<R> {
    val results = LinkedList<R>()
    val matcher = pattern.matcher(s)
    while (matcher.find()) {
      results.add(action(matcher))
    }
    return results
  }
}

class FindAllInMultiImpl<R : Any>(val patternStrings: List<Pair<String, (matcher: Matcher) -> R>>) : FindAllIn<R> {
  val findAllIns = patternStrings.map { FindAllInImpl<R>(it.first, it.second) }

  override fun findAllIn(s: String): List<R> {
    val results = LinkedList<R>()

    findAllIns.forEach { findAllIn ->
      results.addAll(findAllIn.findAllIn(s))
    }

    return results
  }
}

data class ChatMessage(
  val header: Header,
  //val personName: String? = null,
  //val companyName: String? = null,
  val phone: List<PhoneNumber> = ArrayList(),
  val url: List<Url> = ArrayList(),
  val linkedin: List<Url> = ArrayList(),
  val email: List<Email> = ArrayList(),
  val lines: List<String> = ArrayList()
) {
  companion object {
    @JvmStatic
    fun fromLines(lines: List<String>): ChatMessage {
      if (lines.isEmpty()) throw IllegalArgumentException("fromLines called with zero lines")
      val (header, headerRemainder) = Header.matchAgainst(lines.first())
      if (header == null) throw IllegalArgumentException("fromLines: first line didn't match a header.")
      val phone = LinkedList<PhoneNumber>()
      val url = LinkedList<Url>()
      val email = LinkedList<Email>()
      lines.forEachIndexed { i, line ->
        val s = when {
          i == 0 -> headerRemainder
          else -> line
        }
        phone.addAll(PhoneNumber.findAllIn(s))
        url.addAll(Url.findAllIn(s))
        email.addAll(Email.findAllIn(s))
      }
      return ChatMessage(
        header = header,
        //personName = header.fromName,
        phone = phone,
        url = url.filter{!it.isLinkedIn && it.raw !in listOf("gmail.com")},
        linkedin = url.filter{it.isLinkedIn},
        email = email,
        lines = ArrayList<String>().apply{addAll(lines)} //defensive copy
      )
    }
  }


  data class Header(
    /**
     *  The name of the person who sent the message if specified.
     * Will be null if unspecified, or empty string if the name field was empty.
     */
    val fromName: String? = null,
    /** The name of the person (the current user) that the message was sent to if specified. */
    val toName: String? = null,
  ) {
    companion object :
      MatchAgainst<Header> by MatchAgainstMultiImpl<Header>(
        listOf(
          Pair(HEADER_PATTERN) { matcher ->
            val fromName = matcher.group(4)
            val remainder = matcher.group(5)
            Pair(
              Header(
                fromName = fromName,
              ),
              remainder
            )
          },
          Pair(HEADER_PATTERN_DIRECT_MESSAGE) { matcher ->
            val fromName = matcher.group(4)
            val toName = matcher.group(5)
            val remainder = matcher.group(6)
            Pair(
              Header(
                fromName = fromName,
                toName = toName,
              ),
              remainder
            )
          },
        )
      ) {

    }
  }


  data class PhoneNumber(val raw: String) {
    companion object : FindAllIn<PhoneNumber> by FindAllInMultiImpl(
      listOf(
        Pair(PHONE_PATTERN) { matcher ->
          PhoneNumber(matcher.group(0))
        },
      )
    )
  }

  data class Url(val raw: String) {
    companion object : FindAllIn<Url> by FindAllInMultiImpl(
      listOf(
        Pair(DEFINITE_URL_PATTERN) { matcher ->
          Url(matcher.group(0))
        },
        Pair(PROBABLE_URL_PATTERN) { matcher ->
          Url(matcher.group(1))
        },
        Pair(PROBABLE_URL_PATTERN2) { matcher ->
          Url(matcher.group(1))
        },
        Pair(PROBABLE_URL_PATTERN3) { matcher ->
          Url(matcher.group(1))
        },
      )
    )
    
    val isLinkedIn: Boolean by lazy {
      raw.matches(LINKEDIN_REGEX)
    }
  }

  data class Email(val raw: String) {
    companion object : FindAllIn<Email> by FindAllInMultiImpl(
      listOf(
        Pair(EMAIL_PATTERN) { matcher ->
          Email(matcher.group(0))
        },
      )
    )
  }

}