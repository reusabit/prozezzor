@file:JvmName("ChatParser")

package com.reusabit.prozezzor

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.util.*


/**
 * Closes the input reader when completed.
 */
fun extractMessages(input: BufferedReader): List<ChatMessage> {
  val buffer = LinkedList<String>()
  val results = LinkedList<ChatMessage>()

  input.lines().forEach { line ->
    //println("line: $line")
    val (header, extraText) = ChatMessage.Header.matchAgainst(line)
    if (header != null) {
      processMessage(buffer)?.let { results.add(it) }
      buffer.clear()
      buffer.add(line) //Unconditionally add the header line.
    } else if (!buffer.isEmpty()) { //Only add the non-header line to the buffer if we have seen a previous header. Otherwise, this line is probably notification text at the start of the chat.
      buffer.add(line)
    }
  }

  //last message:
  processMessage(buffer)?.let { results.add(it) }

  results.forEach {
    //println("message: $it")
  }
  return results
}

private fun processMessage(buffer: LinkedList<String>): ChatMessage? {
  if (buffer.isEmpty()) return null
  else return ChatMessage.fromLines(buffer)
}