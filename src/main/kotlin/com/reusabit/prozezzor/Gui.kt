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

import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Image
import java.awt.Insets
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.net.URI
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.filechooser.FileNameExtensionFilter


private val HELP_URL = "https://reusabit.com/prozezzor-help"

/**
 * The gui displays a dialog box that is initially populated with the values calculated previously,
 * either from command line processing or defaults. The user can change these, and run the processing
 * as many times as desired.
 */
fun doGui(programOptions: ProgramOptions.Builder) {
  val gui = Gui(programOptions)
}

fun makeInsets(
  top: Int = 0,
  left: Int = 0,
  bottom: Int = 0,
  right: Int = 0,
) = Insets(top, left, bottom, right)

fun makeInsets(h: Int = 0, v: Int = 0) = makeInsets(v, h, v, h)
fun makeInsets(all: Int = 0) = makeInsets(all, all)

fun makeConstraints(
  gridx: Int,
  gridy: Int,
  gridwidth: Int = 1,
  gridheight: Int = 1,
  weightx: Double = 0.0,
  weighty: Double = 0.0,
  anchor: Int = GridBagConstraints.WEST,
  fill: Int = GridBagConstraints.NONE,
  insets: Insets = makeInsets(),
  ipadx: Int = 0,
  ipady: Int = 0,
): GridBagConstraints = GridBagConstraints(
  gridx,
  gridy,
  gridwidth,
  gridheight,
  weightx,
  weighty,
  anchor,
  fill,
  insets,
  ipadx,
  ipady
)

fun getNoticeText(): String {
  val cl = Thread.currentThread().contextClassLoader
  val stream = cl.getResourceAsStream("NOTICE.txt")
  val reader = stream?.reader(Charsets.UTF_8)
  val noticeText = reader?.readText() ?: ""
  return noticeText
}

//class NoticesDialog(owner: JFrame) : JDialog(owner, "Third Party License Notices", true) {
//  init {
//    layout = GridBagLayout()
//
//    contentPane.apply {
//      add(JLabel("Third Party License Notices:"), makeConstraints(0, 0, insets = makeInsets(20)))
//
//      val cl = Thread.currentThread().contextClassLoader
//      val stream = cl.getResourceAsStream("NOTICE.txt")
//      val reader = stream?.reader(Charsets.UTF_8)
//      val noticeText = reader?.readText() ?: ""
//
//      add(
//        JScrollPane((JTextPane().apply {
//          isEditable = false
//          text = "Something shorter\nanotherline\n".repeat(1000000)
//        })),
//        makeConstraints(0, 1, insets = makeInsets(20))
//      )
//
//
//      add(
//        JButton("Close").apply {
//          addActionListener { e ->
//            this@NoticesDialog.isVisible = false
//            this@NoticesDialog.dispose()
//          }
//
//          this@NoticesDialog.defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
//          this@NoticesDialog.rootPane.defaultButton = this
//        },
//        makeConstraints(0, 2, insets = makeInsets(20))
//      )
//    }
//
//    val desktopSize = Toolkit.getDefaultToolkit().screenSize
//    val MAX_WIDTH = 600
//    val MAX_HEIGHT = 1000
//    val width = minOf(desktopSize.width, MAX_WIDTH)
//    val height = minOf(desktopSize.height, MAX_HEIGHT)
//    val x = maxOf(0, desktopSize.width / 2 - width / 2)
//    val y = maxOf(0, desktopSize.height / 2 - height / 2)
//    setBounds(x, y, width, height)
//
//    defaultCloseOperation = JDialog.DISPOSE_ON_CLOSE
//    isModal = true
//  }
//}


class Gui(val programOptions: ProgramOptions.Builder) : JFrame("Prozezzor") {
  val inputDirLabel = JLabel().apply {
    text = "Input Directory:"
  }

  val inputDirField = JTextField().apply {
    text = programOptions.inputDir?.path ?: ""
    toolTipText = "Select or enter the directory containing the chat files"
    preferredSize = Dimension(400, 25)
  }

  val inputDirBrowseButton = JButton().apply {
    text = "Browse..."
  }

  val outputFileLabel = JLabel().apply {
    text = "Output File:"
  }

  val outputFileField = JTextField().apply {
    text = programOptions.outputFile?.path ?: ""
    toolTipText = "Select or enter the output file name"
    preferredSize = Dimension(400, 25)
  }

  val outputFileBrowseButton = JButton().apply {
    text = "Browse..."
  }

  val runButton = JButton().apply {
    text = "Run"
  }

  val exitButton = JButton().apply {
    text = "Exit"
  }

  val help = JMenuItem("Instructions")
  val notices = JMenuItem("Third Party License Notices")
  val about = JMenuItem("About")
  val menuBar = JMenuBar().apply {
    add(JMenu("Help").apply {
      add(help)
      add(notices)
      add(about)
    })
  }

  init {
    val desktopSize = Toolkit.getDefaultToolkit().screenSize
    val MAX_WIDTH = 700
    val MAX_HEIGHT = 230
    val width = minOf(desktopSize.width, MAX_WIDTH)
    val height = minOf(desktopSize.height, MAX_HEIGHT)
    val x = maxOf(0, desktopSize.width / 2 - width / 2)
    val y = maxOf(0, desktopSize.height / 2 - height / 2)
    setBounds(x, y, width, height)
    defaultCloseOperation = DISPOSE_ON_CLOSE

    jMenuBar = menuBar

    help.addActionListener { displayHelp() }
    notices.addActionListener { displayNotices() }
    about.addActionListener { aboutDialog() }

    iconImages = mutableListOf(
      ImageIO.read(javaClass.getResource("/icon16x16.png")) as Image,
      ImageIO.read(javaClass.getResource("/icon32x32.png")) as Image,
      ImageIO.read(javaClass.getResource("/icon48x48.png")) as Image,
      ImageIO.read(javaClass.getResource("/icon128x128.png")) as Image,
    )

    val mainPanel = JPanel().apply {
      contentPane.add(this, BorderLayout.CENTER)

      layout = GridBagLayout().apply {
        //border = EtchedBorder()//makeInsets(all = 10))
      }
    }

    mainPanel.add(
      inputDirLabel,
      makeConstraints(gridx = 1, gridy = 1, insets = makeInsets(5))
    )

    mainPanel.add(
      inputDirField,
      makeConstraints(
        gridx = 2,
        gridy = 1,
        fill = GridBagConstraints.HORIZONTAL,
        weightx = 1.0,
        insets = makeInsets(5)
      )
    )

    mainPanel.add(
      inputDirBrowseButton,
      makeConstraints(gridx = 3, gridy = 1, insets = makeInsets(5))
    )

    mainPanel.add(
      outputFileLabel,
      makeConstraints(gridx = 1, gridy = 2, insets = makeInsets(5))
    )

    mainPanel.add(
      outputFileField,
      makeConstraints(
        gridx = 2,
        gridy = 2,
        fill = GridBagConstraints.HORIZONTAL,
        weightx = 1.0,
        insets = makeInsets(5)
      )
    )

    mainPanel.add(
      outputFileBrowseButton,
      makeConstraints(gridx = 3, gridy = 2, insets = makeInsets(5))
    )

    //Empty panel
    mainPanel.add(
      JPanel().apply {
        setSize(50, 30)
      },
      makeConstraints(3, 3)
    )

    mainPanel.add(
      JPanel().apply {
        layout = GridBagLayout()

        //Empty
        add(
          JPanel().apply {
            setPreferredSize(Dimension(10, 10))
          },
          makeConstraints(1, 1, weightx = 1.0, fill = GridBagConstraints.HORIZONTAL)
        )

        add(
          runButton,
          makeConstraints(2, 1, insets = makeInsets(5))
        )

        add(
          exitButton,
          makeConstraints(3, 1, insets = makeInsets(5))
        )
      },
      makeConstraints(
        gridx = 2,
        gridy = 4,
        fill = GridBagConstraints.HORIZONTAL,
      )
    )

    inputDirBrowseButton.addActionListener { e ->
      val chooser = when {
        File(inputDirField.text).isDirectory -> JFileChooser(inputDirField.text)
        else -> JFileChooser()
      }
      .apply {
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
      }
      val result = chooser.showOpenDialog(this)
      if (result == JFileChooser.APPROVE_OPTION) {
        inputDirField.text = chooser.selectedFile.path
      }
    }

    outputFileBrowseButton.addActionListener { e ->
      val fieldValue = File(outputFileField.text)
      val parent = when {
        fieldValue.isDirectory -> fieldValue
        fieldValue.parentFile?.isDirectory ?: false -> fieldValue.parentFile
        else -> null
      }
      val chooser = JFileChooser()
      .apply {
        //fileSelectionMode = JFileChooser.SAVE_DIALOG // This makes it not work, shows folder selection rather than file selection.
        if (parent != null) currentDirectory = parent
        val filter = FileNameExtensionFilter("xlsx files (*.xlsx)", "xlsx")
        addChoosableFileFilter(filter)
        fileFilter = filter
      }

      while (true) {
        val result = chooser.showSaveDialog(this)
        val newFileName = when {
          chooser.selectedFile.path.endsWith(".xlsx") -> chooser.selectedFile.path
          else -> chooser.selectedFile.path + ".xlsx"
        }
        if (result == JFileChooser.APPROVE_OPTION) {
          //JFileChooser doesn't append the extension if it is not entered:
          val response = promptForOverwrite(
            this,
            File(newFileName),
            msgFileName = "output file"
          )
          if (response.toProceed()) {
            outputFileField.text = newFileName
            programOptions.overwriteOutputFile = response.toOverwrite()
          }
          if (!response.toRepeatFileSelection()) break
        }
      }
    }

    exitButton.addActionListener { e ->
      this.dispose()
      System.exit(0)
    }

    runButton.addActionListener { e ->
      programOptions.inputDir = File(inputDirField.text)
      programOptions.outputFile = File(outputFileField.text)
      val promptResult = promptForOverwrite(
        parent = this,
        file = programOptions.outputFile!!,
        overwrite = programOptions.overwriteOutputFile!!,
        msgFileName = "output file",
        includeCancelOption = false,
      )
      if (!promptResult.toProceed()) return@addActionListener
      programOptions.overwriteOutputFile = promptResult.toOverwrite()
      val errors = programOptions.validate()
      if (!errors.isEmpty()) {
        JOptionPane.showMessageDialog(
          this,
          "Unable to process. There were errors:\n\n${errors.joinToString("\n")}",
          "Errors",
          JOptionPane.ERROR_MESSAGE
        )
      }
      else {
        val programOptions0 = programOptions.build()
        val error = doProcessingCatchExceptions(programOptions0)
        if (error == null) {
          val options = arrayOf("Ok", "Open")
          val choice = JOptionPane.showOptionDialog(
            this,
            "The spreadsheet was created successfully. Saved to ${programOptions.outputFile}",
            "Success",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
          )
          if (choice == 1) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
              Desktop.getDesktop().open(programOptions0.outputFile);
          }
        }
        else {
          processingError(error)
        }
      }
    }


    addWindowListener(object : WindowAdapter() {
      override fun windowClosing(e: WindowEvent?) {
        System.exit(0)
      }
    })

    isVisible = true //Needs to be last.
  }

  /** Display an error dialog. Must be called from the event thread. */
  fun processingError(message: String) {
    JOptionPane.showMessageDialog(
      this,
      "There was an error during processing:\n\n${message}",
      "Error",
      JOptionPane.ERROR_MESSAGE
    )
  }

  /** Display the about dialog. Must be called from the event thread. */
  fun aboutDialog() {
    val version = VersionService().version
    JOptionPane.showMessageDialog(
      this,
      "Prozezzor, version $version, Copyright 2021 Reusabit Software LLC",
      "About",
      JOptionPane.PLAIN_MESSAGE
    )
  }

  /** Launch a text editor with the notices file. Must be called from the event thread. */
  fun displayNotices() {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
      val tempFile = File.createTempFile("prozezzor-third-party-notice-", ".txt")
      val text = getNoticeText()
      tempFile.writeText(text, Charsets.UTF_8)
      Desktop.getDesktop().edit(tempFile)
    }
    else {
      JOptionPane.showMessageDialog(this, "Unable to open text editor", "Error", JOptionPane.ERROR_MESSAGE)
    }
  }

  /** Launch the browser to display help page. Must be called from the event thread. */
  fun displayHelp() {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      Desktop.getDesktop().browse(URI(HELP_URL));
    }
    else {
      JOptionPane.showMessageDialog(this, "Unable to open web browser", "Error", JOptionPane.ERROR_MESSAGE)
    }
  }
}

