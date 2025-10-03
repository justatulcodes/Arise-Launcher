package com.expeknow.ariselauncher.ui.screens.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.expeknow.ariselauncher.data.model.TaskLinkType
import java.util.Calendar

object Utils {

    fun Drawable.toImageBitmap(): ImageBitmap {
        if (this is BitmapDrawable) {
            bitmap?.let { return it.asImageBitmap() }
        }

        val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            createBitmap(1, 1)
        } else {
            createBitmap(intrinsicWidth, intrinsicHeight)
        }

        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap.asImageBitmap()
    }

    fun getTodayStartTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getTodayEndTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun extractYouTubeVideoId(url: String): String? {
        val patterns = listOf(
            "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([^&\\n?#]+)".toRegex(),
            "youtube\\.com/watch\\?.*v=([^&\\n?#]+)".toRegex()
        )

        for (pattern in patterns) {
            val matchResult = pattern.find(url)
            if (matchResult != null) {
                return matchResult.groupValues[1]
            }
        }
        return null
    }

    fun openLink(context: Context, url: String, linkType: TaskLinkType) {
        try {
            val intent = when (linkType) {
                TaskLinkType.VIDEO -> {
                    // Try to open YouTube links in YouTube app first
                    if (url.contains("youtube.com") || url.contains("youtu.be")) {
                        val videoId = extractYouTubeVideoId(url)
                        if (videoId != null) {
                            // Try YouTube app first
                            val youtubeIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                            youtubeIntent.setPackage("com.google.android.youtube")

                            // Check if YouTube app is available
                            if (youtubeIntent.resolveActivity(context.packageManager) != null) {
                                youtubeIntent
                            } else {
                                // Fallback to web browser
                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            }
                        } else {
                            // If we can't extract video ID, open in browser
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        }
                    } else {
                        // Non-YouTube video links, open in browser
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    }
                }
                TaskLinkType.ARTICLE, TaskLinkType.LINK -> {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: open in browser if specific app intent fails
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                // Handle the case where no browser is available
                println("Error opening link: ${ex.message}")
            }
        }
    }



}