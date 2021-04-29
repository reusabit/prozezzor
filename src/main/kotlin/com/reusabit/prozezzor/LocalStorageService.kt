package com.reusabit.prozezzor

import java.io.File
import kotlin.IllegalArgumentException

class LocalStorageServiceException(msg: String? = null, cause: Throwable? = null) : RuntimeException(msg, cause)

/**
 * Utilities for working with files in the application's directory (home/.prozezzor).
 */
class LocalStorageService {
  private val homeDirString: String? =
    System.getProperty("user.home")

  val homeDir: File? = homeDirString?.let { File(homeDirString) }

  val prozzezorDir: File? = homeDir?.resolve(".prozezzor")

  /**
   * Ensure the directory exists or throw exception.
   *
   * @param file directory to create; returned for chaining
   * @param msgName name of this file for purpose of generating exception messages.
   */
  private fun mkdirOrThrow(file: File, msgName: String): File {
    if (file.exists() && !file.isDirectory)
      throw LocalStorageServiceException("The $msgName directory [$file] exists, but is not a directory")

    if (!file.exists())
      file.mkdirs() || throw LocalStorageServiceException("Unable to create the $msgName directory [$file]")

    return file //Return parameter for chaining
  }

  /**
   * Ensure that the directory, relative to the home/.prozezzor directory, is created if need be, and that
   * it is a directory, not a file.
   */
  fun prepareDirectory(relativeDir: String) = prepareDirectory(File(relativeDir))
  fun prepareDirectory(relativeDir: File): File {
    if (relativeDir.isAbsolute)
      throw IllegalArgumentException("The relativeDir [$relativeDir] is not relative.")

    val homeDirString: String =
      System.getProperty("user.home")
      ?: throw LocalStorageServiceException("system user.home property is not available.")

    val homeDir: File = File(homeDirString)

    val prozezzorDir: File = homeDir.resolve(".prozezzor")
    mkdirOrThrow(prozezzorDir, "prozezzor")

    val dir = prozezzorDir.resolve(relativeDir)
    mkdirOrThrow(dir, "prepared")

    return dir
  }

}
