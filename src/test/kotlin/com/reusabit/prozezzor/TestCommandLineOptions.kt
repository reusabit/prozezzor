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

import com.github.ajalt.clikt.core.NoSuchOption
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.nio.file.Files

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
  fun guiModeRelativeInputDir() {
    val args = arrayOf<String>("--gui", "--input-directory", "relativedir")
    assertThatThrownBy{
      val options = harness(args)
    }
    .isInstanceOf(UsageError::class.java)
    .hasMessageContaining("--gui")
    .hasMessageContaining("absolute")
    .hasMessageContaining("--input-directory")
    .hasMessageContaining("relativedir")
  }

  @Test
  fun guiModeRelativeOutputDirectory() {
    val args = arrayOf<String>("--gui", "--output-directory", "relativefile.xlsx")
    assertThatThrownBy{
      val options = harness(args)
    }
    .isInstanceOf(UsageError::class.java)
    .hasMessageContaining("--gui")
    .hasMessageContaining("absolute")
    .hasMessageContaining("--output-file")
    .hasMessageContaining("relativefile.xlsx")
  }

  @Test
  fun noArgs() {
    val args = arrayOf<String>()
    assertThatThrownBy{
      val options = harness(args)
    }
    .isInstanceOf(UsageError::class.java)
    .hasMessageContaining("--yes")
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
    val args = arrayOf("-I", "-y")
    val options = harness(args)
    assertThat(options.mode).isEqualTo(AppMode.NON_INTERACTIVE)
  }

  @Test
  fun noninteractiveModeLongForm() {
    val args = arrayOf("--non-interactive", "--yes")
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

  @Test
  fun noninteractiveNoYes() {
    val args = arrayOf("-I")
    assertThatThrownBy {
      val options = harness(args)
    }
    .isInstanceOf(UsageError::class.java)
    .hasMessageContaining("--yes")
  }

  @Test
  fun noninteractiveForce() {
    val args = arrayOf("-y", "-f")
    val options = harness(args)
    assertThat(options.overwriteOutputFile).isTrue()
  }

  @Test
  fun noninteractiveForceNoYes() {
    val args = arrayOf("-f")
    assertThatThrownBy {
      val options = harness(args)
    }
    .isInstanceOf(UsageError::class.java)
    .hasMessageContaining("--yes")
  }

}