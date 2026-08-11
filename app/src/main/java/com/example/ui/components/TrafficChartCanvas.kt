package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DownloadColor
import com.example.ui.theme.UploadColor

@Composable
fun TrafficChartCanvas(
    trafficHistory: List<Pair<Long, Long>>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(vertical = 8.dp)
    ) {
        if (trafficHistory.isEmpty() || trafficHistory.size < 2) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeColor = Color.Gray.copy(alpha = 0.2f)
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2f
                )
            }
            return@Box
        }

        val maxDown = trafficHistory.maxOfOrNull { it.first }?.coerceAtLeast(1024L) ?: 1024L
        val maxUp = trafficHistory.maxOfOrNull { it.second }?.coerceAtLeast(1024L) ?: 1024L
        val maxSpeed = maxOf(maxDown, maxUp).toFloat()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val stepX = width / (trafficHistory.size - 1).coerceAtLeast(1)

            // Grid lines
            val gridColor = Color.Gray.copy(alpha = 0.15f)
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height * i / gridLines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            // Path for Download
            val downPath = Path()
            val downFillPath = Path()
            downFillPath.moveTo(0f, height)

            trafficHistory.forEachIndexed { index, (down, _) ->
                val x = index * stepX
                val normalizedY = height - ((down.toFloat() / maxSpeed) * (height * 0.85f))
                if (index == 0) {
                    downPath.moveTo(x, normalizedY)
                    downFillPath.lineTo(x, normalizedY)
                } else {
                    val prevX = (index - 1) * stepX
                    val prevDown = trafficHistory[index - 1].first
                    val prevY = height - ((prevDown.toFloat() / maxSpeed) * (height * 0.85f))
                    val controlX1 = prevX + (x - prevX) / 2
                    val controlX2 = prevX + (x - prevX) / 2
                    downPath.cubicTo(controlX1, prevY, controlX2, normalizedY, x, normalizedY)
                    downFillPath.cubicTo(controlX1, prevY, controlX2, normalizedY, x, normalizedY)
                }
            }
            downFillPath.lineTo((trafficHistory.size - 1) * stepX, height)
            downFillPath.close()

            // Draw Download Gradient Fill
            drawPath(
                path = downFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DownloadColor.copy(alpha = 0.3f),
                        DownloadColor.copy(alpha = 0.0f)
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Download Curve Line
            drawPath(
                path = downPath,
                color = DownloadColor,
                style = Stroke(width = 3.dp.toPx())
            )

            // Path for Upload
            val upPath = Path()
            val upFillPath = Path()
            upFillPath.moveTo(0f, height)

            trafficHistory.forEachIndexed { index, (_, up) ->
                val x = index * stepX
                val normalizedY = height - ((up.toFloat() / maxSpeed) * (height * 0.85f))
                if (index == 0) {
                    upPath.moveTo(x, normalizedY)
                    upFillPath.lineTo(x, normalizedY)
                } else {
                    val prevX = (index - 1) * stepX
                    val prevUp = trafficHistory[index - 1].second
                    val prevY = height - ((prevUp.toFloat() / maxSpeed) * (height * 0.85f))
                    val controlX1 = prevX + (x - prevX) / 2
                    val controlX2 = prevX + (x - prevX) / 2
                    upPath.cubicTo(controlX1, prevY, controlX2, normalizedY, x, normalizedY)
                    upFillPath.cubicTo(controlX1, prevY, controlX2, normalizedY, x, normalizedY)
                }
            }
            upFillPath.lineTo((trafficHistory.size - 1) * stepX, height)
            upFillPath.close()

            // Draw Upload Gradient Fill
            drawPath(
                path = upFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UploadColor.copy(alpha = 0.25f),
                        UploadColor.copy(alpha = 0.0f)
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw Upload Curve Line
            drawPath(
                path = upPath,
                color = UploadColor,
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
    }
}

fun formatSpeed(bytesPerSec: Long): String {
    return when {
        bytesPerSec >= 1024 * 1024 * 1024 -> String.format("%.2f GB/s", bytesPerSec.toDouble() / (1024 * 1024 * 1024))
        bytesPerSec >= 1024 * 1024 -> String.format("%.2f MB/s", bytesPerSec.toDouble() / (1024 * 1024))
        bytesPerSec >= 1024 -> String.format("%.1f KB/s", bytesPerSec.toDouble() / 1024)
        else -> "$bytesPerSec B/s"
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes.toDouble() / (1024 * 1024 * 1024))
        bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
        bytes >= 1024 -> String.format("%.1f KB", bytes.toDouble() / 1024)
        else -> "$bytes B"
    }
}
