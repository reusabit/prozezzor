package com.reusabit.prozezzor

import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.core.PrintHelpMessage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

private fun harness(argv: Array<String>): ProgramOptions {

  val prozezzor = Prozezzor(
    inputDirDefault = Files.createTempDirectory("prozezzor-test-idd").toFile().path,
    outputDirDefault = Files.createTempDirectory("prozezzor-test-odd").toFile().path,
  )
  prozezzor.parse(argv)
  return prozezzor.toCommandLineOptions()
}

/**
 * If the --gui (-g) flag is set, then the appliations runs a gui.
 *
 * If --interactive (-i)
 * is set, then the application will run in command-line interactive mode, prompting for
 * each option. (-g and -i are mutually exclusive.)
 *
 * If any command line option is specified, but not gui or interactive, then
 * the application runs in non-interactive command-line mode, using the specified
 * arguments or defaults for unspecified arguments as options.
 *
 * There is also a help mode (--help) that displays detailed help information and exits.
 *
 * There is also a usage mode that is activated when invalid arguments are supplied.
 * Display short-form usage information and exit.
 */
class TestProgramOptionsMode {
  @Test
  fun guiModeLongForm() {
    val args = arrayOf("--gui")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.GUI)
  }

  @Test
  fun guiModeShortForm() {
    val args = arrayOf("-g")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.GUI)
  }

  @Test
  fun noArgs() {
    val args = arrayOf<String>()
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.NON_INTERACTIVE)
  }


  @Test
  fun interactiveModeLongForm() {
    val args = arrayOf("--interactive")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.INTERACTIVE)
  }

  @Test
  fun interactiveModeShortForm() {
    val args = arrayOf("-i")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.INTERACTIVE)
  }

  @Test
  fun noninteractiveModeShortForm() {
    val args = arrayOf("-I")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.NON_INTERACTIVE)
  }

  @Test
  fun noninteractiveModeLongForm() {
    val args = arrayOf("--non-interactive")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.NON_INTERACTIVE)
  }

  @Test
  fun invalidOption() {
    val args = arrayOf("--thisIsNotAValidOption")
    assertThatThrownBy {
      val options = harness(args)
    }
      .isInstanceOf(NoSuchOption::class.java)
  }

  @Test
  fun help() {
    val args = arrayOf("--help")
    assertThatThrownBy {
      val options = harness(args)
    }
      .isInstanceOf(PrintHelpMessage::class.java)
  }

}