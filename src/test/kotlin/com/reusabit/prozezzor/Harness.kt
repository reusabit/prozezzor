package com.reusabit.prozezzor

import java.nio.file.Files

fun harness(argv: Array<String>, inputDirDefault: String? = null, outputFileNameDefault: String? = null, outputDirDefault: String? = null): ProgramOptions.Builder {
  val prozezzor = Prozezzor(
    inputDirDefault = inputDirDefault ?: Files.createTempDirectory("prozezzor-test-idd").toFile().path,
    outputFileDefault = outputFileNameDefault ?: Prozezzor.OUTPUT_FILE_NAME_DEFAULT,
    outputDirDefault = outputDirDefault ?: Files.createTempDirectory("prozezzor-test-odd").toFile().path,
  )
  prozezzor.parse(argv)
  return prozezzor.programOptions
}
