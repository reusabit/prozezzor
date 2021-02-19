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
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Hyperlink
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFHyperlink
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.nio.file.FileAlreadyExistsException



private val NAME_COL = 0
private val EMAIL_COL = 1
private val PHONE_COL = 2
private val LINKEDIN_COL = 3
private val WEBSITE_COL = 4

private val HEADER_ROW = 0


private val NAME_WIDTH = 25
private val EMAIL_WIDTH = 30
private val PHONE_WIDTH = 18
private val LINKEDIN_WIDTH = 50
private val WEBSITE_WIDTH = 50

fun buildSpreadsheet(records: List<Record>): XSSFWorkbook {
  val workbook = XSSFWorkbook()
  val sheet = workbook.createSheet("Contacts")

  val hyperlinkStyle = workbook.createCellStyle().apply {
    //fillForegroundColor = IndexedColors.BLUE.index
    //fillPattern = FillPatternType.SOLID_FOREGROUND
    setFont(workbook.createFont().apply {
      underline = XSSFFont.U_SINGLE
      color = IndexedColors.LIGHT_BLUE.index
    })
  }

  /**
   * href only supports url hyperlinks currently
   */
  fun Row.createCell(i: Int, value: String, href: String? = null) {
    val cell = this.createCell(i)
    cell.setCellValue(value)
    if (href != null) {
      val href0 = when {
        href.startsWith("https://") || href.startsWith("http://") -> href
        else -> "http://${href}"
      }
      val hyperlink = this.sheet.workbook.creationHelper.createHyperlink(HyperlinkType.URL).apply { address = href0 }
      cell.hyperlink = hyperlink
      cell.cellStyle = hyperlinkStyle
    }
  }

  sheet.setColumnWidth(NAME_COL, NAME_WIDTH * 256)
  sheet.setColumnWidth(EMAIL_COL, EMAIL_WIDTH * 256)
  sheet.setColumnWidth(PHONE_COL, PHONE_WIDTH * 256)
  sheet.setColumnWidth(LINKEDIN_COL, LINKEDIN_WIDTH * 256)
  sheet.setColumnWidth(WEBSITE_COL, WEBSITE_WIDTH * 256)

  val headerRow = sheet.createRow(0)
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
    record.linkedin?.let { row.createCell(LINKEDIN_COL, value = it, href = it) }
    record.website?.let { row.createCell(WEBSITE_COL, value = it, href = it) }
  }

  return workbook
}

fun writeSpreadsheet(workbook: XSSFWorkbook, file: File, overwrite: Boolean = false) {
  if (file.isDirectory) throw FileAlreadyExistsException("The file [${file}] is a directory.")
  if (file.exists() && !overwrite)
    throw FileAlreadyExistsException("The file [${file}] already exists and overwrite is not enabled.")
  FileOutputStream(file).use {
    workbook.write(it)
  }
}