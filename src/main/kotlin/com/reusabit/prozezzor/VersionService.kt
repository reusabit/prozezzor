package com.reusabit.prozezzor

import java.util.*

class VersionService {
  private val properties: Properties? by lazy {
    javaClass.getResourceAsStream("/version.properties")
    ?.let { inputStream ->
      inputStream.use { inputStream ->
        Properties().apply {
          load(inputStream)
        }
      }
    }
  }

  val version: String? by lazy {
    properties?.get("version") as String?
  }
}
