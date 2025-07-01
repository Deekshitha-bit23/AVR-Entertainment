package com.deeksha.avrentertainment.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class ReportData(
    val title: String,
    val totalSpent: Double,
    val totalBudget: Double,
    val departmentData: List<DepartmentReportItem>,
    val filters: ReportFilters
)

data class DepartmentReportItem(
    val department: String,
    val budget: Double,
    val spent: Double,
    val percentage: Double
)

data class ReportFilters(
    val dateRange: String,
    val department: String,
    val project: String
)

class ExportUtils {
    companion object {
        fun exportToPDF(
            context: Context,
            reportData: ReportData,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "AVR_Report_$timestamp.pdf"
                
                // Use app-specific external storage for Android 10+
                val downloadsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Reports")
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                
                // Create PDF
                val writer = PdfWriter(file)
                val pdf = PdfDocument(writer)
                val document = Document(pdf, PageSize.A4)
                
                // Title
                document.add(
                    Paragraph("AVR ENTERTAINMENT - EXPENSE REPORT")
                        .setFontSize(18f)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(20f)
                )
                
                // Report Info
                document.add(
                    Paragraph("Generated on: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}")
                        .setFontSize(10f)
                        .setTextAlignment(TextAlignment.RIGHT)
                )
                
                // Filters
                if (reportData.filters.dateRange != "All Time" || 
                    reportData.filters.department != "All Departments" || 
                    reportData.filters.project != "All Projects") {
                    document.add(Paragraph("Filters Applied:").setBold().setMarginTop(10f))
                    if (reportData.filters.dateRange != "All Time") {
                        document.add(Paragraph("• Date Range: ${reportData.filters.dateRange}").setFontSize(10f))
                    }
                    if (reportData.filters.department != "All Departments") {
                        document.add(Paragraph("• Department: ${reportData.filters.department}").setFontSize(10f))
                    }
                    if (reportData.filters.project != "All Projects") {
                        document.add(Paragraph("• Project: ${reportData.filters.project}").setFontSize(10f))
                    }
                }
                
                // Summary
                document.add(
                    Paragraph("SUMMARY")
                        .setBold()
                        .setFontSize(14f)
                        .setMarginTop(20f)
                        .setMarginBottom(10f)
                )
                
                val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(40f, 30f, 30f)))
                summaryTable.setWidth(UnitValue.createPercentValue(100f))
                
                summaryTable.addHeaderCell(Cell().add(Paragraph("Metric").setBold()))
                summaryTable.addHeaderCell(Cell().add(Paragraph("Amount").setBold()))
                summaryTable.addHeaderCell(Cell().add(Paragraph("Percentage").setBold()))
                
                summaryTable.addCell("Total Budget")
                summaryTable.addCell(formatIndianCurrency(reportData.totalBudget))
                summaryTable.addCell("100%")
                
                summaryTable.addCell("Total Spent")
                summaryTable.addCell(formatIndianCurrency(reportData.totalSpent))
                val utilizationPercentage = if (reportData.totalBudget > 0) (reportData.totalSpent / reportData.totalBudget) * 100 else 0.0
                summaryTable.addCell("${String.format("%.1f", utilizationPercentage)}%")
                
                summaryTable.addCell("Remaining Budget")
                val remaining = reportData.totalBudget - reportData.totalSpent
                summaryTable.addCell(formatIndianCurrency(remaining))
                val remainingPercentage = if (reportData.totalBudget > 0) (remaining / reportData.totalBudget) * 100 else 0.0
                summaryTable.addCell("${String.format("%.1f", remainingPercentage)}%")
                
                document.add(summaryTable)
                
                // Department Breakdown
                document.add(
                    Paragraph("DEPARTMENT BREAKDOWN")
                        .setBold()
                        .setFontSize(14f)
                        .setMarginTop(20f)
                        .setMarginBottom(10f)
                )
                
                val departmentTable = Table(UnitValue.createPercentArray(floatArrayOf(25f, 25f, 25f, 25f)))
                departmentTable.setWidth(UnitValue.createPercentValue(100f))
                
                // Header
                departmentTable.addHeaderCell(Cell().add(Paragraph("Department").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY))
                departmentTable.addHeaderCell(Cell().add(Paragraph("Budget").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY))
                departmentTable.addHeaderCell(Cell().add(Paragraph("Spent").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY))
                departmentTable.addHeaderCell(Cell().add(Paragraph("Utilization").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY))
                
                // Data rows
                reportData.departmentData.forEach { item ->
                    departmentTable.addCell(item.department)
                    departmentTable.addCell(formatIndianCurrency(item.budget))
                    departmentTable.addCell(formatIndianCurrency(item.spent))
                    departmentTable.addCell("${String.format("%.1f", item.percentage)}%")
                }
                
                document.add(departmentTable)
                
                // Footer
                document.add(
                    Paragraph("This report was generated automatically by AVR Entertainment Expense Management System")
                        .setFontSize(8f)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(30f)
                )
                
                document.close()
                onSuccess(file)
                
            } catch (e: Exception) {
                android.util.Log.e("ExportPDF", "Error creating PDF: ${e.message}", e)
                onError("Failed to create PDF: ${e.message}")
            }
        }
        
        fun exportToExcel(
            context: Context,
            reportData: ReportData,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "AVR_Report_$timestamp.xlsx"
                
                // Use app-specific external storage for Android 10+
                val downloadsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Reports")
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, fileName)
                
                val workbook = XSSFWorkbook()
                
                // Create Summary Sheet
                val summarySheet = workbook.createSheet("Summary")
                
                // Header style
                val headerStyle = workbook.createCellStyle()
                val headerFont = workbook.createFont()
                headerFont.bold = true
                headerFont.fontHeightInPoints = 12
                headerStyle.setFont(headerFont)
                
                // Title
                var rowNum = 0
                val titleRow = summarySheet.createRow(rowNum++)
                val titleCell = titleRow.createCell(0)
                titleCell.setCellValue("AVR ENTERTAINMENT - EXPENSE REPORT")
                titleCell.cellStyle = headerStyle
                
                // Generated date
                rowNum++
                val dateRow = summarySheet.createRow(rowNum++)
                dateRow.createCell(0).setCellValue("Generated on:")
                dateRow.createCell(1).setCellValue(SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
                
                // Filters
                if (reportData.filters.dateRange != "All Time" || 
                    reportData.filters.department != "All Departments" || 
                    reportData.filters.project != "All Projects") {
                    rowNum++
                    val filterHeaderRow = summarySheet.createRow(rowNum++)
                    val filterHeaderCell = filterHeaderRow.createCell(0)
                    filterHeaderCell.setCellValue("Filters Applied:")
                    filterHeaderCell.cellStyle = headerStyle
                    
                    if (reportData.filters.dateRange != "All Time") {
                        val filterRow = summarySheet.createRow(rowNum++)
                        filterRow.createCell(0).setCellValue("Date Range:")
                        filterRow.createCell(1).setCellValue(reportData.filters.dateRange)
                    }
                    if (reportData.filters.department != "All Departments") {
                        val filterRow = summarySheet.createRow(rowNum++)
                        filterRow.createCell(0).setCellValue("Department:")
                        filterRow.createCell(1).setCellValue(reportData.filters.department)
                    }
                    if (reportData.filters.project != "All Projects") {
                        val filterRow = summarySheet.createRow(rowNum++)
                        filterRow.createCell(0).setCellValue("Project:")
                        filterRow.createCell(1).setCellValue(reportData.filters.project)
                    }
                }
                
                // Summary section
                rowNum += 2
                val summaryHeaderRow = summarySheet.createRow(rowNum++)
                val summaryHeaderCell = summaryHeaderRow.createCell(0)
                summaryHeaderCell.setCellValue("SUMMARY")
                summaryHeaderCell.cellStyle = headerStyle
                
                val summaryTableHeader = summarySheet.createRow(rowNum++)
                summaryTableHeader.createCell(0).setCellValue("Metric")
                summaryTableHeader.createCell(1).setCellValue("Amount")
                summaryTableHeader.createCell(2).setCellValue("Percentage")
                
                val budgetRow = summarySheet.createRow(rowNum++)
                budgetRow.createCell(0).setCellValue("Total Budget")
                budgetRow.createCell(1).setCellValue(formatIndianCurrency(reportData.totalBudget))
                budgetRow.createCell(2).setCellValue("100%")
                
                val spentRow = summarySheet.createRow(rowNum++)
                spentRow.createCell(0).setCellValue("Total Spent")
                spentRow.createCell(1).setCellValue(formatIndianCurrency(reportData.totalSpent))
                val utilizationPercentage = if (reportData.totalBudget > 0) (reportData.totalSpent / reportData.totalBudget) * 100 else 0.0
                spentRow.createCell(2).setCellValue("${String.format("%.1f", utilizationPercentage)}%")
                
                val remainingRow = summarySheet.createRow(rowNum++)
                remainingRow.createCell(0).setCellValue("Remaining Budget")
                val remaining = reportData.totalBudget - reportData.totalSpent
                remainingRow.createCell(1).setCellValue(formatIndianCurrency(remaining))
                val remainingPercentage = if (reportData.totalBudget > 0) (remaining / reportData.totalBudget) * 100 else 0.0
                remainingRow.createCell(2).setCellValue("${String.format("%.1f", remainingPercentage)}%")
                
                // Create Department Sheet
                val deptSheet = workbook.createSheet("Department Breakdown")
                
                // Department header
                var deptRowNum = 0
                val deptHeaderRow = deptSheet.createRow(deptRowNum++)
                val deptHeaderCell = deptHeaderRow.createCell(0)
                deptHeaderCell.setCellValue("DEPARTMENT BREAKDOWN")
                deptHeaderCell.cellStyle = headerStyle
                
                deptRowNum++
                val deptTableHeader = deptSheet.createRow(deptRowNum++)
                deptTableHeader.createCell(0).setCellValue("Department")
                deptTableHeader.createCell(1).setCellValue("Budget")
                deptTableHeader.createCell(2).setCellValue("Spent")
                deptTableHeader.createCell(3).setCellValue("Utilization %")
                
                // Department data
                reportData.departmentData.forEach { item ->
                    val deptRow = deptSheet.createRow(deptRowNum++)
                    deptRow.createCell(0).setCellValue(item.department)
                    deptRow.createCell(1).setCellValue(formatIndianCurrency(item.budget))
                    deptRow.createCell(2).setCellValue(formatIndianCurrency(item.spent))
                    deptRow.createCell(3).setCellValue("${String.format("%.1f", item.percentage)}%")
                }
                
                // Auto-size columns
                for (i in 0..3) {
                    summarySheet.autoSizeColumn(i)
                    deptSheet.autoSizeColumn(i)
                }
                
                // Write to file
                val outputStream = FileOutputStream(file)
                workbook.write(outputStream)
                outputStream.close()
                workbook.close()
                
                onSuccess(file)
                
            } catch (e: Exception) {
                android.util.Log.e("ExportExcel", "Error creating Excel: ${e.message}", e)
                onError("Failed to create Excel: ${e.message}")
            }
        }
        
        fun shareFile(context: Context, file: File, mimeType: String) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "AVR Entertainment Expense Report")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(Intent.createChooser(intent, "Share Report"))
            } catch (e: Exception) {
                android.util.Log.e("ShareFile", "Error sharing file: ${e.message}", e)
            }
        }
        
        private fun formatIndianCurrency(amount: Double): String {
            return when {
                amount >= 10000000 -> "₹${String.format("%.1f", amount / 10000000)} Cr"
                amount >= 100000 -> "₹${String.format("%.1f", amount / 100000)} L"
                amount >= 1000 -> "₹${String.format("%.1f", amount / 1000)} K"
                else -> "₹${String.format("%.0f", amount)}"
            }
        }
    }
} 