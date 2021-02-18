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


fun toRecordBuilder(message: ChatMessage): Record.Builder {
  val builder = Record.Builder(
    name = message.header.fromName,
    //companyName = message.companyName,
    primaryPhoneNumber = message.phone.firstOrNull()?.raw,
    website = message.url.firstOrNull()?.raw,
    primaryEmail = message.email.firstOrNull()?.raw,
    linkedin = message.linkedin.firstOrNull()?.raw
  )

  return builder
}

fun disambiguate(messages: List<ChatMessage>): List<Record> {
  val results = LinkedHashSet<Record.Builder>()
  messages.forEach{message->
    results.add(toRecordBuilder(message))
  }

  return results
  .filter{it.primaryPhoneNumber != null || it.primaryEmail != null || it.linkedin != null || it.website != null}
  .map{it.build()}
}
