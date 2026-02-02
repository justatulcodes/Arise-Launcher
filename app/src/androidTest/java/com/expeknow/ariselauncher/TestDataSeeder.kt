package com.expeknow.ariselauncher

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.expeknow.ariselauncher.data.database.AriseLauncherDatabase
import com.expeknow.ariselauncher.data.model.DaysOfWeek
import com.expeknow.ariselauncher.data.model.PointsLog
import com.expeknow.ariselauncher.data.model.PointsLogType
import com.expeknow.ariselauncher.data.model.Task
import com.expeknow.ariselauncher.data.model.TaskCategory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.UUID
import kotlin.random.Random

/**
 * Test Data Seeder for Tasks and Points Log
 *
 * This test class seeds the database with random test data for testing purposes.
 * It creates tasks and points log entries with backdated timestamps spanning
 * the last 180 days (6 months).
 *
 * RUN THIS TEST TO SEED DATA:
 * ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.expeknow.ariselauncher.TestDataSeeder
 *
 * Or run the individual test method from Android Studio by clicking the green play button.
 */
@RunWith(AndroidJUnit4::class)
class TestDataSeeder {

    private lateinit var database: AriseLauncherDatabase
    private lateinit var context: Context

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

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = AriseLauncherDatabase.getDatabase(context)
    }

    @After
    fun cleanup() {
        // Don't close the database - we want to keep the seeded data
    }

    /**
     * Main test method to seed all test data.
     * Run this test to populate the database with test data.
     */
    @Test
    fun seedTestData() = runBlocking {
        seedTasks()
        seedPointsLog()
    }

    /**
     * Seeds the tasks table with random data from the past 180 days.
     * Creates a mix of completed and incomplete tasks across all categories.
     */
    @Test
    fun seedTasks() = runBlocking {
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
                    title = "$title ${if (i > titles.size) "#${i / titles.size + 1}" else ""}".trim(),
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

        println("✅ Seeded $totalTasks tasks across all categories")
        println("   - Completed tasks: ${tasks.count { it.isCompleted }}")
        println("   - Incomplete tasks: ${tasks.count { !it.isCompleted }}")
        println("   - Recurring tasks: ${tasks.count { it.isRepeated }}")
    }

    /**
     * Seeds the points_log table with corresponding entries for completed tasks
     * and some additional spent points entries.
     */
    @Test
    fun seedPointsLog() = runBlocking {
        val pointsLogDao = database.pointsLogDao()
        val pointsLogs = mutableListOf<PointsLog>()

        val now = System.currentTimeMillis()
        val sixMonthsAgo = now - (180L * 24 * 60 * 60 * 1000)

        // Generate points earned logs (simulating completed tasks)
        val earnedLogCount = Random.nextInt(100, 151)
        for (i in 0 until earnedLogCount) {
            val taskId = UUID.randomUUID().toString()
            val taskNames = (peopleTaskTitles + opportunityTaskTitles + skillsTaskTitles + personalTaskTitles)
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
        val rewardNames = listOf(
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

        for (i in 0 until spentLogCount) {
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

        println("✅ Seeded ${pointsLogs.size} points log entries")
        println("   - Earned entries: $earnedLogCount (Total: $totalEarned points)")
        println("   - Spent entries: $spentLogCount (Total: $totalSpent points)")
        println("   - Net balance: ${totalEarned - totalSpent} points")
    }

    /**
     * Clears all test data from tasks and points_log tables.
     * Run this if you want to reset and re-seed.
     */
    @Test
    fun clearAllTestData() = runBlocking {
        database.taskDao().deleteAllTasks()
        database.pointsLogDao().resetAllPointsLog()
        println("✅ Cleared all tasks and points log entries")
    }

    /**
     * Seeds a smaller dataset for quick testing.
     * Creates 20 tasks and 30 points log entries.
     */
    @Test
    fun seedSmallDataset() = runBlocking {
        val taskDao = database.taskDao()
        val pointsLogDao = database.pointsLogDao()

        val now = System.currentTimeMillis()
        val oneMonthAgo = now - (30L * 24 * 60 * 60 * 1000)

        // Create 20 quick tasks
        val quickTasks = listOf(
            "Quick meeting" to TaskCategory.PEOPLE,
            "Review code" to TaskCategory.SKILLS,
            "Apply for role" to TaskCategory.OPPORTUNITY,
            "Morning run" to TaskCategory.PERSONAL,
            "Call client" to TaskCategory.PEOPLE,
            "Learn Kotlin" to TaskCategory.SKILLS,
            "Submit proposal" to TaskCategory.OPPORTUNITY,
            "Meditate" to TaskCategory.PERSONAL,
            "Team sync" to TaskCategory.PEOPLE,
            "Practice SQL" to TaskCategory.SKILLS,
            "Network event" to TaskCategory.OPPORTUNITY,
            "Gym workout" to TaskCategory.PERSONAL,
            "Mentor session" to TaskCategory.PEOPLE,
            "Read docs" to TaskCategory.SKILLS,
            "Interview prep" to TaskCategory.OPPORTUNITY,
            "Journal" to TaskCategory.PERSONAL,
            "Coffee chat" to TaskCategory.PEOPLE,
            "Build feature" to TaskCategory.SKILLS,
            "Update resume" to TaskCategory.OPPORTUNITY,
            "Plan week" to TaskCategory.PERSONAL
        )

        for ((title, category) in quickTasks) {
            val isCompleted = Random.nextBoolean()
            val createdAt = Random.nextLong(oneMonthAgo, now)
            val completedAt = if (isCompleted) Random.nextLong(createdAt, now) else null

            taskDao.insertTask(
                Task(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = "Test task for quick testing",
                    points = Random.nextInt(5, 26) * 5,
                    category = category,
                    priority = Random.nextInt(1, 6),
                    relatedLinks = emptyList(),
                    isCompleted = isCompleted,
                    createdAt = createdAt,
                    completedAt = completedAt,
                    isRepeated = false,
                    repeatDays = emptyList()
                )
            )
        }

        // Create 30 points log entries
        for (i in 0 until 30) {
            val isEarned = Random.nextFloat() < 0.7f
            val timestamp = Random.nextLong(oneMonthAgo, now)

            pointsLogDao.insertPointsLog(
                PointsLog(
                    id = UUID.randomUUID().toString(),
                    taskId = UUID.randomUUID().toString(),
                    taskName = if (isEarned) "Completed task #$i" else "Reward #$i",
                    type = if (isEarned) PointsLogType.EARNED else PointsLogType.SPENT,
                    points = Random.nextInt(5, 51) * 5,
                    timestamp = timestamp
                )
            )
        }

        println("✅ Seeded small dataset: 20 tasks and 30 points log entries")
    }

    /**
     * Creates a dataset specifically for testing date-based queries.
     * Creates entries for specific dates to test date filtering.
     */
    @Test
    fun seedDateSpecificData() = runBlocking {
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
                        category = TaskCategory.values()[j % TaskCategory.values().size],
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

        println("✅ Seeded date-specific data: ${specificDates.size * 5} tasks with corresponding points logs")
    }

    private fun generateRandomRepeatDays(): List<DaysOfWeek> {
        val allDays = DaysOfWeek.values().toList()
        val numDays = Random.nextInt(1, 5) // 1 to 4 days
        return allDays.shuffled().take(numDays)
    }
}
