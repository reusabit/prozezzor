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
