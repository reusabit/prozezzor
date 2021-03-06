
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



import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.UncheckedIOException
import java.lang.RuntimeException
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.NotDirectoryException
import java.util.*
import org.slf4j.LoggerFactory


class Processing {}
private val logger = LoggerFactory.getLogger(Processing::class.java)!!


/** Returns error message if exception, null otherwise. */
fun doProcessingCatchExceptions(programOptions: ProgramOptions): String? {
  try {
    doProcessing(programOptions)
    return null
  }
  catch (e: UncheckedIOException) {
    if (e.cause is AccessDeniedException) {
      return "Access Denied while attempting to read file [${e.message}]"
    }
    else return "Error: ${e.message}"
  }
  catch (e: Exception) {
    return "Error: [${e.message})"
  }
}

fun doProcessing(programOptions: ProgramOptions) {
  val files = enumerateFiles(programOptions)
  val messages = LinkedList<ChatMessage>()

  files.forEach {
    messages.addAll(processFile(it))
  }

  val messagesDedup = messages.distinctBy { it.lines }

  val records = disambiguate(messagesDedup)
  //records.forEachIndexed{i, record->
  //  println("record [$i]: $record")
  //}

  val workbook = buildSpreadsheet(records, messagesDedup)

  writeSpreadsheet(workbook, programOptions.outputFile, programOptions.overwriteOutputFile)
}

private val MAX_FILES_TO_PROCESS = 2000

/** Thrown if too many files were encountered walking the directory tree. E.g., if the root directory is passed as the input directory. */
class TooManyFilesException(msg: String? = null, cause: Throwable? = null) : RuntimeException(msg, cause)

fun enumerateFiles(programOptions: ProgramOptions, maxFilesToProcess: Int = MAX_FILES_TO_PROCESS): List<File> {
  val results = LinkedList<File>()
  val dir = programOptions.inputDir
  if (!dir.isDirectory) throw NotDirectoryException("dir [$dir] is not a directory")

  var counter = 0
  Files.walk(dir.toPath())
  .map {
    it.toFile().let {
      Pair(it, it.lastModified())
    }
  }
  .sorted(compareBy { it.second }) //last modified
  .map{it.first}
  .forEach {
    if (counter++ > maxFilesToProcess)
      throw TooManyFilesException("Encountered too many files during processing. Max files = $maxFilesToProcess")
    if (it.isFile) {
      if (it.extension == "txt") results.add(it)
    }
  }

  return results
}

fun processFile(file: File): List<ChatMessage> {
  logger.info("Processing [$file]...")
  BufferedReader(FileReader(file)).use { input ->
    return extractMessages(input);
  }
}
