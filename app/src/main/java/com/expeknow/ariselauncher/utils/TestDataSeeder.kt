package com.expeknow.ariselauncher.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.expeknow.ariselauncher.data.database.AriseLauncherDatabase
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

/**
 * Test Data Seeder for Tasks and Points Log
 *
 * This utility seeds the database with random test data for testing purposes.
 * It creates tasks and points log entries with backdated timestamps spanning
 * the last 180 days (6 months).
 *
 * The seeder runs only once - it uses SharedPreferences to track if it has already run.
 * To re-seed data, clear the app data or call [forceReseed].
 */
object TestDataSeeder {

    private const val TAG = "TestDataSeeder"
    private const val PREFS_NAME = "test_data_seeder_prefs"
    private const val KEY_DATA_SEEDED = "data_seeded"

    // Sample task titles for different categories
    private val peopleTaskTitles = listOf(
        "Call mom and check in",
        "Schedule meeting with John",
        "Reply to Sarah's email",
        "Catch up with college friends",
        "Send birthday wishes to Alex",
        "Plan family dinner",
        "Help neighbor with groceries",
        "Mentor junior developer",
        "Network at tech meetup",
        "Write thank you note to boss",
        "Organize team building event",
        "Follow up with client",
        "Schedule dentist appointment",
        "Coffee chat with new colleague"
    )

    private val opportunityTaskTitles = listOf(
        "Apply for senior position",
        "Complete online certification",
        "Attend industry conference",
        "Submit proposal for project",
        "Research investment options",
        "Update LinkedIn profile",
        "Prepare pitch deck",
        "Review job listings",
        "Set up informational interview",
        "Join professional association",
        "Write blog post about work",
        "Create portfolio website",
        "Learn new programming language",
        "Apply for speaking opportunity"
    )

    private val skillsTaskTitles = listOf(
        "Practice Kotlin coroutines",
        "Read Clean Code chapter",
        "Complete Jetpack Compose tutorial",
        "Build side project feature",
        "Watch Android architecture video",
        "Practice SQL queries",
        "Learn Docker basics",
        "Study design patterns",
        "Review data structures",
        "Practice system design",
        "Write unit tests for project",
        "Learn Git advanced features",
        "Study Hilt dependency injection",
        "Practice touch typing",
        "Complete algorithm challenges"
    )

    private val personalTaskTitles = listOf(
        "Morning meditation",
        "30 min workout",
        "Read for 20 minutes",
        "Drink 8 glasses of water",
        "Journal daily thoughts",
        "Practice gratitude",
        "Meal prep for the week",
        "Organize desk",
        "Clean the apartment",
        "Go for evening walk",
        "Practice deep breathing",
        "Digital detox for 1 hour",
        "Plan weekly schedule",
        "Review monthly budget",
        "Learn a new recipe",
        "Fix bathroom tap",
        "Update passwords",
        "Backup phone photos"
    )

    private val taskDescriptions = listOf(
        "This is an important task that needs to be completed.",
        "Remember to focus on quality over speed.",
        "Make sure to take breaks while working on this.",
        "This task supports my long-term goals.",
        "Breaking this down into smaller steps might help.",
        "This has been on my list for a while now.",
        "High priority item that needs attention.",
        "Quick task that should take 15 minutes.",
        "This will require deep focus time.",
        "",
        "",
        "" // Some tasks without description
    )

    private val rewardNames = listOf(
        "Coffee break reward",
        "Gaming session",
        "Movie night",
        "Snack reward",
        "Social media time",
        "Shopping reward",
        "Day off reward",
        "Restaurant treat",
        "New book purchase",
        "Hobby time"
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Seeds test data if it hasn't been seeded before.
     * Call this from Application.onCreate()
     */
    fun seedIfNeeded(context: Context) {
        val prefs = getPrefs(context)
        if (prefs.getBoolean(KEY_DATA_SEEDED, false)) {
            Log.d(TAG, "Test data already seeded, skipping...")
            return
        }

        Log.d(TAG, "Seeding test data...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                seedAllData(context)
                prefs.edit().putBoolean(KEY_DATA_SEEDED, true).apply()
                Log.d(TAG, "✅ Test data seeding completed successfully!")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to seed test data", e)
            }
        }
    }

    /**
     * Force re-seed data even if it was already seeded.
     * This clears existing data and seeds fresh data.
     */
    fun forceReseed(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_DATA_SEEDED, false).apply()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AriseLauncherDatabase.getDatabase(context)
                database.taskDao().deleteAllTasks()
                database.pointsLogDao().resetAllPointsLog()
                Log.d(TAG, "Cleared existing data")

                seedAllData(context)
                prefs.edit().putBoolean(KEY_DATA_SEEDED, true).apply()
                Log.d(TAG, "✅ Force re-seed completed successfully!")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to force re-seed test data", e)
            }
        }
    }

    private suspend fun seedAllData(context: Context) {
        val database = AriseLauncherDatabase.getDatabase(context)
        seedTasks(database)
        seedPointsLog(database)
    }

    /**
     * Seeds the tasks table with random data from the past 180 days.
     * Creates a mix of completed and incomplete tasks across all categories.
     */
    private suspend fun seedTasks(database: AriseLauncherDatabase) {
        val taskDao = database.taskDao()
        val tasks = mutableListOf<Task>()

        val now = System.currentTimeMillis()
        val sixMonthsAgo = now - (180L * 24 * 60 * 60 * 1000) // 180 days ago

        // Create tasks for each category
        val allTaskData = mapOf(
            TaskCategory.PEOPLE to peopleTaskTitles,
            TaskCategory.OPPORTUNITY to opportunityTaskTitles,
            TaskCategory.SKILLS to skillsTaskTitles,
            TaskCategory.PERSONAL to personalTaskTitles
        )

        var totalTasks = 0

        // Generate 150+ tasks spread across 180 days
        for ((category, titles) in allTaskData) {
            // Create 35-45 tasks per category
            val numTasks = Random.nextInt(35, 46)

            for (i in 0 until numTasks) {
                val title = titles[i % titles.size]
                val description = taskDescriptions.random()
                val points = Random.nextInt(5, 51) * 5 // Points in multiples of 5 (5-250)
                val priority = Random.nextInt(1, 6) // Priority 1-5

                // Random creation date within last 180 days
                val createdAt = Random.nextLong(sixMonthsAgo, now)

                // 60% chance of being completed
                val isCompleted = Random.nextFloat() < 0.60f

                // If completed, completion date is after creation date but before now
                val completedAt = if (isCompleted) {
                    val minCompletionTime = createdAt + (30 * 60 * 1000) // At least 30 mins after creation
                    val maxCompletionTime = minOf(createdAt + (7L * 24 * 60 * 60 * 1000), now) // Within 7 days or now
                    if (maxCompletionTime > minCompletionTime) {
                        Random.nextLong(minCompletionTime, maxCompletionTime)
                    } else {
                        now
                    }
                } else {
                    null
                }

                // 20% chance of being a recurring task
                val isRepeated = Random.nextFloat() < 0.20f
                val repeatDays = if (isRepeated) {
                    generateRandomRepeatDays()
                } else {
                    emptyList()
                }

                val task = Task(
                    id = UUID.randomUUID().toString(),
                    title = "$title ${if (i >= titles.size) "#${i / titles.size + 1}" else ""}".trim(),
                    description = description,
                    points = points,
                    category = category,
                    priority = priority,
                    relatedLinks = emptyList(),
                    isCompleted = isCompleted,
                    createdAt = createdAt,
                    completedAt = completedAt,
                    isRepeated = isRepeated,
                    repeatDays = repeatDays
                )

                tasks.add(task)
                totalTasks++
            }
        }

        // Insert all tasks
        for (task in tasks) {
            taskDao.insertTask(task)
        }

        Log.d(TAG, "✅ Seeded $totalTasks tasks across all categories")
        Log.d(TAG, "   - Completed tasks: ${tasks.count { it.isCompleted }}")
        Log.d(TAG, "   - Incomplete tasks: ${tasks.count { !it.isCompleted }}")
        Log.d(TAG, "   - Recurring tasks: ${tasks.count { it.isRepeated }}")
    }

    /**
     * Seeds the points_log table with corresponding entries for completed tasks
     * and some additional spent points entries.
     */
    private suspend fun seedPointsLog(database: AriseLauncherDatabase) {
        val pointsLogDao = database.pointsLogDao()
        val pointsLogs = mutableListOf<PointsLog>()

        val now = System.currentTimeMillis()
        val sixMonthsAgo = now - (180L * 24 * 60 * 60 * 1000)

        // Generate points earned logs (simulating completed tasks)
        val earnedLogCount = Random.nextInt(100, 151)
        val taskNames = peopleTaskTitles + opportunityTaskTitles + skillsTaskTitles + personalTaskTitles

        repeat(earnedLogCount) {
            val taskId = UUID.randomUUID().toString()
            val taskName = taskNames.random()
            val points = Random.nextInt(5, 51) * 5 // Points in multiples of 5
            val timestamp = Random.nextLong(sixMonthsAgo, now)

            pointsLogs.add(
                PointsLog(
                    id = UUID.randomUUID().toString(),
                    taskId = taskId,
                    taskName = taskName,
                    type = PointsLogType.EARNED,
                    points = points,
                    timestamp = timestamp
                )
            )
        }

        // Generate points spent logs (rewards claimed)
        val spentLogCount = Random.nextInt(20, 41)

        repeat(spentLogCount) {
            val taskId = "reward_${UUID.randomUUID()}"
            val taskName = rewardNames.random()
            val points = Random.nextInt(10, 101) * 10 // Spent points in multiples of 10
            val timestamp = Random.nextLong(sixMonthsAgo, now)

            pointsLogs.add(
                PointsLog(
                    id = UUID.randomUUID().toString(),
                    taskId = taskId,
                    taskName = taskName,
                    type = PointsLogType.SPENT,
                    points = points,
                    timestamp = timestamp
                )
            )
        }

        // Sort by timestamp for realistic insertion order
        val sortedLogs = pointsLogs.sortedBy { it.timestamp }

        // Insert all points logs
        pointsLogDao.insertPointsLogs(sortedLogs)

        val totalEarned = pointsLogs.filter { it.type == PointsLogType.EARNED }.sumOf { it.points }
        val totalSpent = pointsLogs.filter { it.type == PointsLogType.SPENT }.sumOf { it.points }

        Log.d(TAG, "✅ Seeded ${pointsLogs.size} points log entries")
        Log.d(TAG, "   - Earned entries: $earnedLogCount (Total: $totalEarned points)")
        Log.d(TAG, "   - Spent entries: $spentLogCount (Total: $totalSpent points)")
        Log.d(TAG, "   - Net balance: ${totalEarned - totalSpent} points")
    }

    /**
     * Seeds date-specific data for testing date-based queries.
     * Creates entries for specific dates (today, yesterday, last week, etc.)
     */
    suspend fun seedDateSpecificData(context: Context) {
        val database = AriseLauncherDatabase.getDatabase(context)
        val taskDao = database.taskDao()
        val pointsLogDao = database.pointsLogDao()

        val calendar = Calendar.getInstance()

        // Create tasks for specific dates
        val specificDates = listOf(
            "Today" to 0,
            "Yesterday" to -1,
            "Last week" to -7,
            "Two weeks ago" to -14,
            "Last month" to -30,
            "Two months ago" to -60,
            "Three months ago" to -90
        )

        for ((label, daysAgo) in specificDates) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, daysAgo)
            calendar.set(Calendar.HOUR_OF_DAY, Random.nextInt(8, 20))
            calendar.set(Calendar.MINUTE, Random.nextInt(0, 60))

            val timestamp = calendar.timeInMillis

            // Create 3 completed tasks and 2 incomplete tasks for each date
            for (j in 1..5) {
                val isCompleted = j <= 3
                val taskId = UUID.randomUUID().toString()

                taskDao.insertTask(
                    Task(
                        id = taskId,
                        title = "Task from $label #$j",
                        description = "Created $daysAgo days ago for date testing",
                        points = 25 * j,
                        category = TaskCategory.entries[j % TaskCategory.entries.size],
                        priority = j,
                        relatedLinks = emptyList(),
                        isCompleted = isCompleted,
                        createdAt = timestamp,
                        completedAt = if (isCompleted) timestamp + (60 * 60 * 1000) else null,
                        isRepeated = false,
                        repeatDays = emptyList()
                    )
                )

                // Add points log for completed tasks
                if (isCompleted) {
                    pointsLogDao.insertPointsLog(
                        PointsLog(
                            id = UUID.randomUUID().toString(),
                            taskId = taskId,
                            taskName = "Task from $label #$j",
                            type = PointsLogType.EARNED,
                            points = 25 * j,
                            timestamp = timestamp + (60 * 60 * 1000)
                        )
                    )
                }
            }
        }

        Log.d(TAG, "✅ Seeded date-specific data: ${specificDates.size * 5} tasks with corresponding points logs")
    }

    private fun generateRandomRepeatDays(): List<DaysOfWeek> {
        val allDays = DaysOfWeek.entries.toList()
        val numDays = Random.nextInt(1, 5) // 1 to 4 days
        return allDays.shuffled().take(numDays)
    }
}
