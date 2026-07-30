package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.AppUsageInfo
import com.example.data.model.DashboardStats
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportExporter(private val context: Context) {

    fun generateCsvReport(stats: DashboardStats, apps: List<AppUsageInfo>): File {
        val file = File(context.cacheDir, "screen_time_report_${System.currentTimeMillis()}.csv")
        val writer = file.bufferedWriter()

        writer.write("SCREEN TIME TRACKER REPORT\n")
        writer.write("Generated Date,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
        writer.write("Total Screen Time Today (min),${stats.totalTimeTodayMs / (1000 * 60)}\n")
        writer.write("Total Screen Time Yesterday (min),${stats.totalTimeYesterdayMs / (1000 * 60)}\n")
        writer.write("Daily Average (min),${stats.averageDailyUsageMs / (1000 * 60)}\n")
        writer.write("Phone Unlocks,${stats.unlockCount}\n")
        writer.write("Notifications Received,${stats.notificationsCount}\n\n")

        writer.write("App Name,Package Name,Category,Today (min),Yesterday (min),Launches,Percentage (%),Trend (%)\n")
        for (app in apps) {
            val appMin = app.todayUsageMs / (1000 * 60)
            val yestMin = app.yesterdayUsageMs / (1000 * 60)
            writer.write("\"${app.appName}\",\"${app.packageName}\",\"${app.category.displayName}\",$appMin,$yestMin,${app.launchCount},${String.format(Locale.US, "%.1f", app.percentageOfTotal)},${String.format(Locale.US, "%.1f", app.usageTrendPercent)}\n")
        }

        writer.flush()
        writer.close()
        return file
    }

    fun generateJsonReport(stats: DashboardStats, apps: List<AppUsageInfo>): File {
        val file = File(context.cacheDir, "screen_time_report_${System.currentTimeMillis()}.json")
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"reportTitle\": \"Screen Time Analysis\",\n")
        sb.append("  \"generatedAt\": \"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())}\",\n")
        sb.append("  \"dashboard\": {\n")
        sb.append("    \"totalTodayMinutes\": ${stats.totalTimeTodayMs / (1000 * 60)},\n")
        sb.append("    \"totalYesterdayMinutes\": ${stats.totalTimeYesterdayMs / (1000 * 60)},\n")
        sb.append("    \"averageDailyMinutes\": ${stats.averageDailyUsageMs / (1000 * 60)},\n")
        sb.append("    \"unlocks\": ${stats.unlockCount},\n")
        sb.append("    \"notifications\": ${stats.notificationsCount}\n")
        sb.append("  },\n")
        sb.append("  \"apps\": [\n")
        apps.forEachIndexed { index, app ->
            sb.append("    {\n")
            sb.append("      \"appName\": \"${app.appName}\",\n")
            sb.append("      \"packageName\": \"${app.packageName}\",\n")
            sb.append("      \"category\": \"${app.category.displayName}\",\n")
            sb.append("      \"todayMinutes\": ${app.todayUsageMs / (1000 * 60)},\n")
            sb.append("      \"launches\": ${app.launchCount},\n")
            sb.append("      \"percentage\": ${String.format(Locale.US, "%.1f", app.percentageOfTotal)}\n")
            sb.append("    }${if (index < apps.size - 1) "," else ""}\n")
        }
        sb.append("  ]\n")
        sb.append("}\n")

        file.writeText(sb.toString())
        return file
    }

    fun generatePdfReport(stats: DashboardStats, apps: List<AppUsageInfo>): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 20f
        paint.isFakeBoldText = true

        canvas.drawText("SCREEN TIME TRACKER REPORT", 40f, 50f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        val dateStr = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generated: $dateStr", 40f, 75f, paint)

        paint.color = Color.DKGRAY
        canvas.drawRect(40f, 90f, 555f, 170f, paint)

        paint.color = Color.WHITE
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("Today's Screen Time: ${stats.totalTimeTodayMs / (1000 * 60)} minutes", 55f, 120f, paint)
        canvas.drawText("Phone Unlocks: ${stats.unlockCount}  |  Notifications: ${stats.notificationsCount}", 55f, 145f, paint)

        paint.color = Color.BLACK
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText("Top Applications Used Today:", 40f, 200f, paint)

        paint.textSize = 11f
        paint.isFakeBoldText = false
        var y = 230f
        for ((index, app) in apps.take(15).withIndex()) {
            val min = app.todayUsageMs / (1000 * 60)
            canvas.drawText("${index + 1}. ${app.appName} (${app.category.displayName}) - $min min (${app.launchCount} launches)", 45f, y, paint)
            y += 24f
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "screen_time_report_${System.currentTimeMillis()}.pdf")
        val os = FileOutputStream(file)
        pdfDocument.writeTo(os)
        pdfDocument.close()
        os.close()

        return file
    }

    fun shareFile(file: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Share Screen Time Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
