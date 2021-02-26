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

import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileAlreadyExistsException

private val LINE_HEIGHT_POINTS = 12.0F
private val ROW_MARGIN_POINTS = 4.0F

private val NAME_WIDTH = 25
private val EMAIL_WIDTH = 30
private val PHONE_WIDTH = 18
private val LINKEDIN_WIDTH = 50
private val WEBSITE_WIDTH = 50

private val TIME_WIDTH = 10
private val MESSAGE_WIDTH = 75

fun <T : Any?> corequisite(vararg values: T) = values.all { it == null } || values.all { it != null }

/**
 * only supports url hyperlinks currently
 */
fun Row.createCell(i: Int, value: String, href: String? = null, style: XSSFCellStyle? = null, wrap: Boolean? = null) {
  val cell = this.createCell(i)
  cell.setCellValue(value)
  if (href != null) {
    val href0 = when {
      href.startsWith("https://") || href.startsWith("http://") -> href
      else -> "http://${href}"
    }
    val hyperlink = this.sheet.workbook.creationHelper.createHyperlink(HyperlinkType.URL).apply { address = href0 }
    cell.hyperlink = hyperlink
  }
  if (style != null) cell.cellStyle = style

}


fun buildSpreadsheet(records: List<Record>, messages: List<ChatMessage>): XSSFWorkbook {
  val workbook = XSSFWorkbook()

  val hyperlinkStyle = workbook.createCellStyle().apply {
    //fillForegroundColor = IndexedColors.BLUE.index
    //fillPattern = FillPatternType.SOLID_FOREGROUND
    setFont(workbook.createFont().apply {
      underline = XSSFFont.U_SINGLE
      color = IndexedColors.LIGHT_BLUE.index
    })
  }

  val wrapStyle = workbook.createCellStyle().apply {
    wrapText = true
  }

  buildContactsSheet(
    workbook = workbook,
    hyperlinkStyle = hyperlinkStyle,
    records = records
  )

  buildMessagesSheet(workbook, messages, wrapStyle = wrapStyle)

  return workbook
}

fun buildContactsSheet(workbook: XSSFWorkbook, hyperlinkStyle: XSSFCellStyle, records: List<Record>) {
  val NAME_COL = 0
  val EMAIL_COL = 1
  val PHONE_COL = 2
  val LINKEDIN_COL = 3
  val WEBSITE_COL = 4

  val HEADER_ROW = 0

  val sheet = workbook.createSheet("Contacts")

  sheet.setColumnWidth(NAME_COL, NAME_WIDTH * 256)
  sheet.setColumnWidth(EMAIL_COL, EMAIL_WIDTH * 256)
  sheet.setColumnWidth(PHONE_COL, PHONE_WIDTH * 256)
  sheet.setColumnWidth(LINKEDIN_COL, LINKEDIN_WIDTH * 256)
  sheet.setColumnWidth(WEBSITE_COL, WEBSITE_WIDTH * 256)

  val headerRow = sheet.createRow(HEADER_ROW)
  headerRow.createCell(NAME_COL, "Name")
  headerRow.createCell(EMAIL_COL, "Email")
  headerRow.createCell(PHONE_COL, "Phone")
  headerRow.createCell(LINKEDIN_COL, "LinkedIn Profile")
  headerRow.createCell(WEBSITE_COL, "Website")

  records.forEachIndexed { i, record ->
    val row = sheet.createRow(i + HEADER_ROW + 1) //start on row after header row
    record.name?.let { row.createCell(NAME_COL, it) }
    record.primaryEmail?.let { row.createCell(EMAIL_COL, it) }
    record.primaryPhoneNumber?.let { row.createCell(PHONE_COL, it) }
    record.linkedin?.let { row.createCell(LINKEDIN_COL, value = it, href = it, style = hyperlinkStyle) }
    record.website?.let { row.createCell(WEBSITE_COL, value = it, href = it, style = hyperlinkStyle) }
  }
}

fun buildMessagesSheet(workbook: XSSFWorkbook, messages: List<ChatMessage>, wrapStyle: XSSFCellStyle) {
  val TIME_COL = 0
  val NAME_COL = 1
  val MESSAGE_COL = 2

  val HEADER_ROW = 0

  val sheet = workbook.createSheet("Messages")

  sheet.setColumnWidth(TIME_COL, TIME_WIDTH * 256)
  sheet.setColumnWidth(NAME_COL, NAME_WIDTH * 256)
  sheet.setColumnWidth(MESSAGE_COL, MESSAGE_WIDTH * 256)


  val headerRow = sheet.createRow(HEADER_ROW)
  headerRow.createCell(TIME_COL, "Time")
  headerRow.createCell(NAME_COL, "Name")
  headerRow.createCell(MESSAGE_COL, "Message")

  messages.forEachIndexed { i, m ->
    val row = sheet.createRow(i + HEADER_ROW + 1) //start on row after header row
    row.heightInPoints = sheet.defaultRowHeightInPoints * m.linesRaw.size + ROW_MARGIN_POINTS
    m.header.time.let { row.createCell(TIME_COL, it) }
    m.header.fromName?.let { row.createCell(NAME_COL, it) }
    m.linesRaw.joinToString("\n").let { row.createCell(MESSAGE_COL, it, style=wrapStyle) }
  }
}

fun writeSpreadsheet(workbook: XSSFWorkbook, file: File, overwrite: Boolean = false) {
  if (file.isDirectory) throw FileAlreadyExistsException("The file [${file}] is a directory.")
  if (file.exists() && !overwrite)
    throw FileAlreadyExistsException("The file [${file}] already exists and overwrite is not enabled.")
  FileOutputStream(file).use {
    workbook.write(it)
  }
}