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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.NotDirectoryException
import java.nio.file.FileAlreadyExistsException

class TestFileIssues {
  @Test
  fun testInputDirNotExist() {
    val outputFile = Files.createTempFile("prozezzor-test-output", ".xlsx").toFile()
    val parentDir = Files.createTempDirectory("prozezzor-test-output-dir").toFile()
    val nonExistentInputDir = parentDir.resolve("non-existent-dir")
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, nonExistentInputDir, outputFile, false)
    assertThatThrownBy {
      doProcessing(programOptions)
    }
    .isInstanceOf(NotDirectoryException::class.java)
    .hasMessageContaining("directory")
  }

  @Test
  fun testInputDirNotExistCatch() {
    val outputFile = Files.createTempFile("prozezzor-test-output", ".xlsx").toFile()
    val parentDir = Files.createTempDirectory("prozezzor-test-output-dir").toFile()
    val nonExistentInputDir = parentDir.resolve("non-existent-dir")
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, nonExistentInputDir, outputFile, false)
    val error = doProcessingCatchExceptions(programOptions)

    assertThat(error)
    .isNotNull()
    .contains("directory")
  }

  @Test
  fun testOutputFileIsDirectoryOverwriteDisabled() {
    val outputFileIsDirectory = Files.createTempDirectory("prozezzor-test-output-file-is-dir").toFile()
    val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileIsDirectory, false)

    assertThatThrownBy {
      doProcessing(programOptions)
    }
    .isInstanceOf(FileAlreadyExistsException::class.java)
    .hasMessageContaining("directory")
  }

  @Test
  fun testOutputFileIsDirectoryOverwriteEnabled() {
    val outputFileIsDirectory = Files.createTempDirectory("prozezzor-test-output-file-is-dir").toFile()
    val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileIsDirectory, true)

    assertThatThrownBy {
      doProcessing(programOptions)
    }
    .isInstanceOf(FileAlreadyExistsException::class.java)
    .hasMessageContaining("directory")
  }

  @Test
  fun testOutputFileNonExistentParent() {
    val outputFileNonExistentParent = Files.createTempDirectory("prozezzor-test-output-non-existent-parent-").toFile()
    .resolve("non-existent-parent/output.xlsx")

    val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileNonExistentParent, false)

    assertThatThrownBy {
      doProcessing(programOptions)
    }
    .isInstanceOf(FileNotFoundException::class.java)
    .hasMessageContaining("path")
  }

  @Test
  fun testOutputFileAlreadyExistsOverwriteDisabled() {
      val outputFileAlreadyExists = Files.createTempFile("prozezzor-test-output-file-", ".xlsx").toFile()
      val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
      val programOptions = ProgramOptions(
        mode = AppMode.NON_INTERACTIVE,
        inputDir = inputDir,
        outputFile = outputFileAlreadyExists,
        overwriteOutputFile = false
      )

      assertThatThrownBy {
        doProcessing(programOptions)
      }
      .isInstanceOf(FileAlreadyExistsException::class.java)
      .hasMessageContaining("overwrite")
    }

  @Test
  fun testOutputFileAlreadyExistsOverwriteEnabled() {
      val outputFileAlreadyExists = Files.createTempFile("prozezzor-test-output-file-", ".xlsx").toFile()
      val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
      val programOptions = ProgramOptions(
        mode = AppMode.NON_INTERACTIVE,
        inputDir = inputDir,
        outputFile = outputFileAlreadyExists,
        overwriteOutputFile = true
      )

      doProcessing(programOptions)
  }
}