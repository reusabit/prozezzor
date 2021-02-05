package com.reusabit.prozezzor

import java.io.File

private fun possibleDir(dir: File): String? {
  if (dir.absoluteFile.isDirectory) return dir.path
  else return null
}

/**
 * Determine the (platform specific) default input directory. Make sure it exists, and return null if
 * there can't find a default.
 */
fun determineInputDirDefault() = possibleDir(File(System.getProperty("user.home")).resolve("Documents/Zoom"))

fun determineOutputDirDefault() = possibleDir(File(System.getProperty("user.home")).resolve("Documents"))
