package com.reusabit.prozezzor

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.NotDirectoryException

class TestFileIssues {
  @Test
  fun testInputDirNotExist() {
    val outputFile = Files.createTempFile("prozezzor-test-output", ".xlsx").toFile()
    val parentDir = Files.createTempDirectory("prozezzor-test-output-dir").toFile()
    val nonExistentInputDir = parentDir.resolve("non-existent-dir")
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, nonExistentInputDir, outputFile)
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
    val programOptions = ProgramOptions(AppMode.NON_INTERACTIVE, nonExistentInputDir, outputFile)
    val error = doProcessingCatchExceptions(programOptions)

    assertThat(error)
    .isNotNull()
    .contains("directory")
  }

  @Test
  fun testOutputFileIsDirectory() {
    val outputFileIsDirectory = Files.createTempDirectory("prozezzor-test-output-file-is-dir").toFile()
    val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
    val programOptions=ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileIsDirectory)

    assertThatThrownBy{
      doProcessing(programOptions)
    }
    .isInstanceOf(FileNotFoundException::class.java)
    .hasMessageContaining("Access")
  }

  @Test
  fun testOutputFileNonExistentParent() {
    val outputFileNonExistentParent = Files.createTempDirectory("prozezzor-test-output-non-existent-parent-").toFile()
    .resolve("non-existent-parent/output.xlsx")

    val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
    val programOptions=ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileNonExistentParent)

    assertThatThrownBy{
      doProcessing(programOptions)
    }
    .isInstanceOf(FileNotFoundException::class.java)
    .hasMessageContaining("path")
  }

  @Test
  fun testOutputFileAlreadyExists() {
    @Test
    fun testOutputFileAlreadyExists() {
      val outputFileAlreadyExists = Files.createTempFile("prozezzor-test-output-file-", ".xlsx").toFile()
      val inputDir = Files.createTempDirectory("prozezzor-test-input-dir").toFile()
      val programOptions=ProgramOptions(AppMode.NON_INTERACTIVE, inputDir, outputFileAlreadyExists)

      //assertThatThrownBy{
        doProcessing(programOptions)
      //}
      //.isInstanceOf(FileNotFoundException::class.java)
      //.hasMessageContaining("path")
    }

  }
}