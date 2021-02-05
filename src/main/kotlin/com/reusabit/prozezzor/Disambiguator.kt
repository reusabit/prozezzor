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
