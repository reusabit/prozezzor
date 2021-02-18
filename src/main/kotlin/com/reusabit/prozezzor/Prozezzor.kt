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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import java.io.File


fun <AllT, EachT, ValueT> OptionWithValues<AllT, EachT, ValueT>.chainIf(
  condition: Boolean,
  action: OptionWithValues<AllT, EachT, ValueT>.() -> OptionWithValues<out AllT, out EachT, out ValueT>
): OptionWithValues<out AllT, out EachT, out ValueT> {
  return if (condition) this.action()
  else this
}

class Prozezzor(
  val inputDirDefault: String? = null,
  val outputFileDefault: String = OUTPUT_FILE_NAME_DEFAULT,
  val outputDirDefault: String? = null,
) : CliktCommand() {
  companion object {
    @JvmStatic
    val OUTPUT_FILE_NAME_DEFAULT = "prozezzor-output.xlsx"
  }

  val gui by option("-g", "--gui", help = "Display a GUI. Mutually exclusive of --interactive.").flag()

  val interactive by option(
    "-i",
    "--interactive",
    help = "(NOT YET IMPLEMENTED) CLI mode, prompt for missing values. Mutually exclusive of --gui."
  )
  .flag("-I", "--non-interactive", default = false)

  val inputDirString by option(
    "-d",
    "--input-directory",
    help = "Location of the directory to process (e.g., the zoom chat archive directory). Must be an absolute path when running in --gui mode."
  )

  val outputFileString by option(
    "-o",
    "--output-file",
    help = "Output file name."
  )

  val outputDir by option(
    "-O",
    "--output-directory",
    help = "Output directory. May only be specified when --output-file-name is a relative path. Must be an absolute path when running in --gui mode."
  )

  val yes by option(
    "-y",
    "--yes",
    help = "Yes. Accept the default values for any unspecified option. Required flag for --non-interactive with unspecified options."
  )
  .flag(default = false)

  val force by option(
    "-f",
    "--force",
    help = "Overwrite the output file if it already exists. (Doesn't have an effect if the output file exists and is a directory.)"
  )
  .flag(default = false)

  val programOptions: ProgramOptions.Builder by lazy { toProgramOptions() }


  /**
   * Performs additional validation (e.g. mutual exclusions), throws errors to cause display
   * of usage information, and returns a simplified options representation.
   *
   * This only works if parse has been called.
   */
  fun toProgramOptions(): ProgramOptions.Builder {
    val mode = when {
      gui -> AppMode.GUI
      interactive -> AppMode.INTERACTIVE
      else -> AppMode.NON_INTERACTIVE
    }

    if (mode == AppMode.INTERACTIVE && yes) throw UsageError("Running in --interactive mode. The --yes flag is not applicable.")

    if (
      mode == AppMode.NON_INTERACTIVE
      && !yes
      && (inputDirString == null || outputDir == null && outputFileString == null)
    ) {
      throw UsageError("Running in --non-interactive, defaults required, and the --yes flag was not specified.")
    }

    val outputDir0 = outputDir ?: outputDirDefault
    val outputDirFile = outputDir0?.let { File(it) }
    val outputFile = File(outputFileString?:outputFileDefault)

    val outputFile0 = when {
      outputFile.isAbsolute -> outputFile
      else -> when {
        mode != AppMode.GUI -> outputFile.absoluteFile
        else -> when {
          outputDirFile == null -> throw UsageError("The program is in --gui mode, --output-directory is not provided, and unable to determine a viable default.")
          else -> when {
            !outputDirFile.isAbsolute -> throw UsageError("The program is in --gui mode and --output-directory [$outputDirFile] is not an absolute path.")
            !outputDirFile.isDirectory -> throw UsageError("The --output-directory [$outputDirFile] is not a directory.")
            else -> outputDirFile.resolve(outputFile)
          }
        }
      }
    }

    val inputDirString0 = inputDirString ?: inputDirDefault
    val inputDir0 = inputDirString0?.let{File(it)}

    if (inputDir0?.isAbsolute == false) throw UsageError("The progam is in --gui mode: --input-directory [$inputDir0] be an absolute path")

    //println("inputDir = [$inputDir]")
    if (mode == AppMode.NON_INTERACTIVE) {
      if (inputDirString0 == null) {
        throw UsageError("--input-directory is required if in non-interactive mode and a default cannot be determined")
      }
    }

    return ProgramOptions.Builder(
      mode = mode,
      inputDir = inputDirString0?.let{File(it)},
      outputFile = outputFile0,
      overwriteOutputFile = force
    )
  }

  override fun run() {
    programOptions // So UssageError exceptions are caught by clikt
  }
}

fun main(argv: Array<String>) {
  val prozezzor = Prozezzor(
    inputDirDefault = determineInputDirDefault(),
    outputDirDefault = determineOutputDirDefault()
  )
  //argv.forEachIndexed { i, arg ->
  //  println("arg[$i]=$arg")
  //}
  prozezzor.main(argv)
  val programOptions = prozezzor.programOptions

  if (programOptions.mode == AppMode.GUI) {
    doGui(programOptions)
    return
  }

  //println("options: $programOptions")

  val programOptions0 = when {
    programOptions.mode == AppMode.NON_INTERACTIVE -> programOptions
    else -> doInteractivePrompts(programOptions)
  }

  //println("options: $programOptions0")

  val programOptionsBuilt = programOptions0.build()
  val error = doProcessingCatchExceptions(programOptionsBuilt)
  if (error == null) println("Success: Data written to file [${programOptionsBuilt.outputFile}]")
  else println("Error: $error")
}


fun doInteractivePrompts(programOptions: ProgramOptions.Builder): ProgramOptions.Builder {
  throw NotImplementedError("Interactive prompts not implemented")
}
