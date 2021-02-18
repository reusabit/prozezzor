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
