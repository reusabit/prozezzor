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

import java.awt.Component
import java.io.File
import javax.swing.JOptionPane

enum class PromptForOverwriteFileType { FILE, DIRECTORY }

/** Response value for promptForOverwrite */
enum class PromptForOverwriteResponse {
  YES, NO, CANCEL, DOESNT_EXIST;

  fun toJOptionPaneValue() = when (this) {
    YES -> JOptionPane.YES_OPTION
    NO -> JOptionPane.NO_OPTION
    CANCEL -> JOptionPane.CANCEL_OPTION
    else -> IllegalArgumentException("Unable to convert [$this] to JOptionPane value, no mapping exists.")
  }

  /** Convert to a truthy value, indicating resultant overwrite value. True iff YES. */
  fun toOverwrite() = this == YES

  /** Convert to boolean indicating whether to proceed. */
  fun toProceed() = this in listOf(YES, DOESNT_EXIST)

  /** Convert to boolean indicating if the file selection should repeat. */
  fun toRepeatFileSelection() = this == NO

  companion object {
    fun fromJOptionPaneValue(value: Int) = when (value) {
      JOptionPane.YES_OPTION -> YES
      JOptionPane.NO_OPTION -> NO
      JOptionPane.CANCEL_OPTION -> CANCEL
      else -> throw IllegalArgumentException("Argument [$value] was not in (YES_OPTION, NO_OPTION, CANCEL_OPTION)")
    }
  }


}

/**
 * Displays prompts for certain cases:
 * If the file exists, but it's the wrong type of file (per fileType), display an error and return CANCEL.
 * If the file exists, but overwrite isn't selected (per overwrite), display an error and return CANCEL.
 * If the file exists, overwrite already specified (per parameter), return YES
 * If the file exists, prompt for whether to overwrite. Return user response.
 * Otherwise, return DOESNT_EXIST
 */
fun promptForOverwrite(
  parent: Component,
  file: File,
  fileType: PromptForOverwriteFileType = PromptForOverwriteFileType.FILE,
  overwrite: Boolean = false,
  msgFileName: String = "file",
  includeCancelOption: Boolean = true
): PromptForOverwriteResponse {
  if (fileType == PromptForOverwriteFileType.FILE && file.exists() && !file.isFile) {
    JOptionPane.showMessageDialog(
      parent,
      "The $msgFileName [${file}] is not a regular file. Please select a different file.",
      "Not a file",
      JOptionPane.ERROR_MESSAGE
    )
    return PromptForOverwriteResponse.CANCEL
  }
  if (fileType == PromptForOverwriteFileType.DIRECTORY && !file.isDirectory) {
    JOptionPane.showMessageDialog(
      parent,
      "The $msgFileName [${file}] is not a directory. Please choose a different file.",
      "Not a directory",
      JOptionPane.ERROR_MESSAGE
    )
    return PromptForOverwriteResponse.CANCEL
  }
  if (file.exists()) {
    if (overwrite) return PromptForOverwriteResponse.YES
    else {
      val response = JOptionPane.showConfirmDialog(
        parent,
        when (fileType) {
          PromptForOverwriteFileType.FILE -> "The $msgFileName [${file}] already exists. Overwrite it?"
          PromptForOverwriteFileType.DIRECTORY -> "The $msgFileName [${file}] already exists. Contents may be overwritten. Use it anyway?"
        },
        "Overwrite?",
        if (includeCancelOption) JOptionPane.YES_NO_CANCEL_OPTION else JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE
      )
      return PromptForOverwriteResponse.fromJOptionPaneValue(response)
    }
  }

  return PromptForOverwriteResponse.DOESNT_EXIST
}