package com.reusabit.prozezzor

import java.io.File
import java.util.*

enum class AppMode {
  /** Display a GUI for options */
  GUI,

  /** Commandline, prompt for options (-i command line option) */
  INTERACTIVE,

  /** Commandline, no prompts, must provide command line options. */
  NON_INTERACTIVE,
}

fun check(errors: MutableCollection<String>, action: ()->Unit) {
  try {
    action()
  }
  catch(e: RuntimeException) {
    e.message?.let{errors.add(it)}
  }
}

data class ProgramOptions(
  val mode: AppMode,
  val inputDir: File,
  val outputFile: File,
) {
  data class Builder(
    var mode: AppMode?,
    var inputDir: File?,
    var outputFile: File?
  ) {
    val modeBuild get() = mode ?: throw RuntimeException("mode is null")
    val inputDirBuild get() = inputDir ?: throw RuntimeException("inputDir is null")
    val inputDirValidated get() = inputDirBuild.also{
      if (!it.isDirectory) throw RuntimeException("The specified input directory [${it}] does not exist or is not a directory")
    }
    val outputFileBuild get() = outputFile ?: throw RuntimeException("outputFile is null")
    val outputFileValidated get() = outputFileBuild.also{
      if (it.parentFile?.isDirectory != true) throw RuntimeException("Unable to write to the output file [${it}]")
    }

    fun validate() = LinkedList<String>().also {
      check (it){modeBuild}
      check (it){inputDirValidated}
      check (it){outputFileValidated}
    }

    fun build() = ProgramOptions(
      mode = modeBuild,
      inputDir = inputDirBuild,
      outputFile = outputFileBuild
    )
  }
}