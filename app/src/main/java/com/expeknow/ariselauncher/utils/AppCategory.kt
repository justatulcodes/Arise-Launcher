package com.expeknow.ariselauncher.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.URL

object AppClassifier {

    private const val TAG = "AppClassifier"

    suspend fun classifyAppsConcurrently(
        context: Context,
        packages: List<String>
    ): Map<String, AppCategory> = coroutineScope {
        packages.map { pkg ->
            async(Dispatchers.IO) {
                pkg to classifyApp(context, pkg)
            }
        }.awaitAll().toMap()
    }


    suspend fun classifyApp(context: Context, packageName: String): AppCategory {
        val pm = context.packageManager
        val appInfo = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return AppCategory.UTILITY
        }

        val playCategory = withContext(Dispatchers.IO) {
            fetchPlayStoreCategory(packageName)
        }

        if (playCategory != null) {
            Log.i(TAG, "Play Store category for ${appInfo.loadLabel(pm)}: $playCategory")
            return mapPlayStoreCategory(playCategory)
        }

        // Fallback for system / sideloaded apps using ApplicationInfo.category
        when (appInfo.category) {
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> return AppCategory.PRODUCTIVITY
            ApplicationInfo.CATEGORY_SOCIAL -> return AppCategory.SOCIAL
            ApplicationInfo.CATEGORY_GAME -> return AppCategory.GAMES
            ApplicationInfo.CATEGORY_VIDEO -> return AppCategory.ENTERTAINMENT
            ApplicationInfo.CATEGORY_AUDIO -> return AppCategory.ENTERTAINMENT
            ApplicationInfo.CATEGORY_IMAGE -> return AppCategory.ENTERTAINMENT
            ApplicationInfo.CATEGORY_NEWS -> return AppCategory.PRODUCTIVITY
            ApplicationInfo.CATEGORY_MAPS -> return AppCategory.UTILITY
            ApplicationInfo.CATEGORY_UNDEFINED -> return AppCategory.MISCELLANEOUS
            ApplicationInfo.CATEGORY_ACCESSIBILITY -> return AppCategory.UTILITY
        }

        return AppCategory.MISCELLANEOUS

    }


    private fun fetchPlayStoreCategory(packageName: String): String? {
        return try {
            val url = "https://play.google.com/store/apps/details?id=$packageName"
            val html = URL(url).readText()
            val regex = Regex("""itemprop="genre"[^>]*>.*?<span[^>]*>(.*?)</span>""")
            val matchResult = regex.find(html)
            val category = matchResult?.groupValues?.get(1)
            if (category == null) {
                Log.d(TAG, "Category not found in HTML for $packageName")
            }
            category
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch Play Store category for $packageName: ${e.message}", e)
            null
        }
    }

    /**
     * Maps official Play Store categories into broad enums.
     */
    private fun mapPlayStoreCategory(category: String): AppCategory {
        return when (category.lowercase()) {
            // Productivity / Utility
            "productivity", "tools", "utilities", "business", "education",
            "books & reference", "events", "house & home", "libraries & demo",
            "parenting", "personalization" -> AppCategory.PRODUCTIVITY

            // Social & Communication
            "communication", "social", "dating" -> AppCategory.SOCIAL

            // Entertainment & Media
            "entertainment", "video players & editors", "music & audio", "photography",
            "news & magazines", "comics", "art & design", "lifestyle" -> AppCategory.ENTERTAINMENT

            // Games (all subgenres go here)
            "action", "adventure", "arcade", "board", "card", "casino", "casual",
            "educational", "music", "puzzle", "racing", "role playing", "simulation",
            "sports", "strategy", "trivia", "word" -> AppCategory.GAMES

            // Shopping & Finance
            "shopping", "food & drink" -> AppCategory.SHOPPING
            "finance" -> AppCategory.UTILITY

            // Health & Travel
            "health & fitness", "medical" -> AppCategory.UTILITY
            "travel & local", "maps & navigation", "auto & vehicles" -> AppCategory.UTILITY

            // Weather
            "weather" -> AppCategory.UTILITY

            else -> AppCategory.ESSENTIAL
        }
    }

    fun getAppPointCost(category: AppCategory): Int {
        return when (category) {
            AppCategory.ESSENTIAL -> 0
            AppCategory.ENTERTAINMENT -> 10
            AppCategory.GAMES -> 25
            AppCategory.SHOPPING -> 5
            AppCategory.UTILITY -> 5
            AppCategory.PRODUCTIVITY -> 0
            AppCategory.SOCIAL -> 30
            AppCategory.MISCELLANEOUS -> 5
        }
    }
}