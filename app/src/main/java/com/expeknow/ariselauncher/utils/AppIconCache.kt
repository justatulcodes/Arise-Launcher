package com.expeknow.ariselauncher.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

class AppIconCache(private val context: Context) {
    private val cacheDir: File
        get() = File(context.cacheDir, "app_icons").apply { mkdirs() }

    suspend fun get(packageName: String): Drawable? = withContext(Dispatchers.IO) {
        val f = File(cacheDir, "$packageName.png")
        if (f.exists()) Drawable.createFromPath(f.absolutePath) else null
    }

    suspend fun put(packageName: String, drawable: Drawable) = withContext(Dispatchers.IO) {
        val f = File(cacheDir, "$packageName.png")
        FileOutputStream(f).use { out ->
            drawableToBitmap(drawable).compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    fun delete(packageName: String) {
        File(cacheDir, "$packageName.png").delete()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) return drawable.bitmap
        val w = drawable.intrinsicWidth.coerceAtLeast(1)
        val h = drawable.intrinsicHeight.coerceAtLeast(1)
        val bmp = createBitmap(w, h)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }
}
