package com.expeknow.ariselauncher.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.expeknow.ariselauncher.ui.screens.apps.AppCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object AppClassifier {

    private const val TAG = "AppClassifier"

    suspend fun classifyApp(context: Context, packageName: String): AppCategory {
        val pm = context.packageManager
        val appInfo = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            //if package name by any chance not found
            return AppCategory.UTILITY
        }

        val playCategory = withContext(Dispatchers.IO) {
            fetchPlayStoreCategory(packageName)
        }

        if (playCategory != null) {
            Log.i(TAG, "Play Store category for ${appInfo.loadLabel(pm)}: $playCategory")
            return mapCategoryToAppCategory(playCategory)
        }

        // Fallback for system / sideloaded apps using ApplicationInfo.category
        return getFallbackCategory(appInfo)

    }

    fun getFallbackCategory(appInfo: ApplicationInfo): AppCategory {
        return when (appInfo.category) {
            ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.PRODUCTIVITY
            ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL_MEDIA
            ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMES
            ApplicationInfo.CATEGORY_VIDEO -> AppCategory.STREAMING
            ApplicationInfo.CATEGORY_AUDIO -> AppCategory.STREAMING
            ApplicationInfo.CATEGORY_IMAGE -> AppCategory.ENTERTAINMENT
            ApplicationInfo.CATEGORY_NEWS -> AppCategory.ENTERTAINMENT
            ApplicationInfo.CATEGORY_MAPS -> AppCategory.UTILITY
            ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppCategory.UTILITY
            ApplicationInfo.CATEGORY_UNDEFINED -> AppCategory.MISCELLANEOUS
            else -> AppCategory.MISCELLANEOUS
        }
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

    fun mapCategoryToAppCategory(category: String): AppCategory {
        return when (category.lowercase()) {
            "productivity", "tools", "business", "education",
            "books & reference", "libraries & demo" -> AppCategory.PRODUCTIVITY

            "communication" -> AppCategory.COMMUNICATION

            "social", "dating" -> AppCategory.SOCIAL_MEDIA

            "video players & editors", "music & audio" -> AppCategory.STREAMING

            "entertainment", "photography", "news & magazines", "comics",
            "art & design", "lifestyle" -> AppCategory.ENTERTAINMENT

            "action", "adventure", "arcade", "board", "card", "casino", "casual",
            "educational", "music", "puzzle", "racing", "role playing", "simulation",
            "sports", "strategy", "trivia", "word" -> AppCategory.GAMES

            "shopping", "food & drink" -> AppCategory.SHOPPING

            "health & fitness", "medical" -> AppCategory.HEALTH

            "finance" -> AppCategory.FINANCE

            "utilities", "weather", "travel & local", "maps & navigation",
            "auto & vehicles", "events", "house & home", "parenting",
            "personalization" -> AppCategory.UTILITY

            else -> AppCategory.ESSENTIAL
        }
    }

    fun getDefaultCategoryString(appInfoCategory: AppCategory): String {
        return when (appInfoCategory) {
            AppCategory.PRODUCTIVITY -> "productivity"
            AppCategory.COMMUNICATION -> "communication"
            AppCategory.SOCIAL_MEDIA -> "social"
            AppCategory.STREAMING -> "video players & editors"
            AppCategory.GAMES -> "action"
            AppCategory.ENTERTAINMENT -> "entertainment"
            AppCategory.HEALTH -> "health & fitness"
            AppCategory.FINANCE -> "finance"
            AppCategory.UTILITY -> "maps & navigation"
            AppCategory.MISCELLANEOUS -> "miscellaneous"
            AppCategory.ESSENTIAL -> "essential"
            AppCategory.SHOPPING -> "shopping"
        }
    }

    fun getAppPointCost(category: AppCategory): Int {
        return when (category) {
            AppCategory.ESSENTIAL -> 0
            AppCategory.PRODUCTIVITY -> 0
            AppCategory.UTILITY -> 0
            AppCategory.HEALTH -> 5
            AppCategory.FINANCE -> 5
            AppCategory.SHOPPING -> 10
            AppCategory.COMMUNICATION -> 15
            AppCategory.ENTERTAINMENT -> 20
            AppCategory.STREAMING -> 25
            AppCategory.SOCIAL_MEDIA -> 30
            AppCategory.GAMES -> 35
            AppCategory.MISCELLANEOUS -> 5
        }
    }
}