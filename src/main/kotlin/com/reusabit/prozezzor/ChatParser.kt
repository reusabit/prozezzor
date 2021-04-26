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
    println("line: $line")
    val (header, extraText) = ChatMessage.Header.matchAgainst(line)
    if (header != null) {
      println("header != null")
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
    println("message: $it")
  }
  return results
}

private fun processMessage(buffer: LinkedList<String>): ChatMessage? {
  if (buffer.isEmpty()) return null
  else return ChatMessage.fromLines(buffer)
}
