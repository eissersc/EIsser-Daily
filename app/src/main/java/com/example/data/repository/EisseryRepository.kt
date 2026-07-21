package com.example.data.repository

import com.example.data.database.UserDao
import com.example.data.database.HydrationLogDao
import com.example.data.database.TransactionDao
import com.example.data.database.WorkoutQuestDao
import com.example.data.database.MedicationReminderDao
import com.example.data.database.UserEntity
import com.example.data.database.HydrationLogEntity
import com.example.data.database.TransactionEntity
import com.example.data.database.WorkoutQuestEntity
import com.example.data.database.MedicationReminderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class EisseryRepository(
    private val userDao: UserDao,
    private val hydrationLogDao: HydrationLogDao,
    private val transactionDao: TransactionDao,
    private val workoutQuestDao: WorkoutQuestDao,
    private val medicationReminderDao: MedicationReminderDao
) {
    val userFlow: Flow<UserEntity?> = userDao.getUserFlow()
    val allLogsFlow: Flow<List<HydrationLogEntity>> = hydrationLogDao.getAllLogsFlow()
    val allTransactionsFlow: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()
    val allQuestsFlow: Flow<List<WorkoutQuestEntity>> = workoutQuestDao.getAllQuestsFlow()
    val allRemindersFlow: Flow<List<MedicationReminderEntity>> = medicationReminderDao.getAllRemindersFlow()

    suspend fun ensureUserExists() {
        val user = userDao.getUserSync()
        if (user == null) {
            userDao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Eisser Student",
                    currentHydrationAmount = 0.0,
                    dailyHydrationGoal = 2000.0,
                    totalPoints = 0,
                    lastHydrationResetDate = ""
                )
            )
        }
    }

    suspend fun logWater(amount: Double) {
        ensureUserExists()
        // Save hydration log
        hydrationLogDao.insertLog(HydrationLogEntity(amount = amount))

        // Update user current amount and reward 10 loyalty points
        val user = userDao.getUserSync()
        if (user != null) {
            val updatedAmount = user.currentHydrationAmount + amount
            val pointsAwarded = 10
            val updatedPoints = user.totalPoints + pointsAwarded
            
            userDao.updateUser(user.copy(
                currentHydrationAmount = updatedAmount,
                totalPoints = updatedPoints
            ))

            // Insert transaction log
            transactionDao.insertTransaction(
                TransactionEntity(
                    productCode = "HYDRATION_LOG",
                    productName = "Logged ${amount.toInt()} mL Water",
                    pointsAdded = pointsAwarded,
                    isRedemption = false
                )
            )
        }
    }

    suspend fun deleteLog(log: HydrationLogEntity) {
        ensureUserExists()
        hydrationLogDao.deleteLog(log)

        val user = userDao.getUserSync()
        if (user != null) {
            val updatedAmount = (user.currentHydrationAmount - log.amount).coerceAtLeast(0.0)
            userDao.updateUser(user.copy(currentHydrationAmount = updatedAmount))
        }
    }

    suspend fun clearLogs() {
        ensureUserExists()
        hydrationLogDao.clearAllLogs()

        val user = userDao.getUserSync()
        if (user != null) {
            userDao.updateUser(user.copy(currentHydrationAmount = 0.0))
        }
    }

    suspend fun updateDailyGoal(newGoal: Double) {
        ensureUserExists()
        val user = userDao.getUserSync()
        if (user != null) {
            userDao.updateUser(user.copy(dailyHydrationGoal = newGoal))
        }
    }

    suspend fun updateProfilePicture(uriString: String?) {
        ensureUserExists()
        val user = userDao.getUserSync()
        if (user != null) {
            userDao.updateUser(user.copy(profilePictureUri = uriString))
        }
    }

    suspend fun updateProfileName(name: String) {
        ensureUserExists()
        val user = userDao.getUserSync()
        if (user != null) {
            userDao.updateUser(user.copy(name = name))
        }
    }

    suspend fun redeemReward(rewardName: String, pointsCost: Int): Result<TransactionEntity> {
        ensureUserExists()
        val user = userDao.getUserSync()!!

        if (user.totalPoints >= pointsCost) {
            // Create transaction (redemption)
            val transaction = TransactionEntity(
                productCode = "REDEEM-${rewardName.replace(" ", "-").uppercase()}",
                productName = "Claimed: $rewardName",
                pointsAdded = -pointsCost,
                isRedemption = true
            )
            transactionDao.insertTransaction(transaction)

            // Update user points
            val updatedPoints = user.totalPoints - pointsCost
            userDao.updateUser(user.copy(totalPoints = updatedPoints))

            return Result.success(transaction)
        } else {
            return Result.failure(IllegalStateException("Insufficient points! You need $pointsCost points but only have ${user.totalPoints}."))
        }
    }

    // New loyalty logic: Complete hydration check assessment rewards 15 points
    suspend fun rewardHydrationAssessment(resultStatus: String) {
        ensureUserExists()
        val user = userDao.getUserSync()!!
        val pointsAwarded = 15
        val updatedPoints = user.totalPoints + pointsAwarded
        
        userDao.updateUser(user.copy(totalPoints = updatedPoints))

        transactionDao.insertTransaction(
            TransactionEntity(
                productCode = "ASSESSMENT_COMPLETED",
                productName = "Hydration Check Assessment",
                pointsAdded = pointsAwarded,
                isRedemption = false
            )
        )
    }

    // Workout Quest Operations
    suspend fun insertWorkoutQuest(quest: WorkoutQuestEntity) {
        workoutQuestDao.insertQuest(quest)
    }

    suspend fun deleteWorkoutQuest(quest: WorkoutQuestEntity) {
        workoutQuestDao.deleteQuest(quest)
    }

    suspend fun completeWorkoutQuest(questId: Int, currentDate: String) {
        ensureUserExists()
        val quest = workoutQuestDao.getQuestById(questId)
        if (quest != null && quest.lastCompletedDate != currentDate) {
            workoutQuestDao.updateQuest(
                quest.copy(
                    isCompleted = true,
                    lastCompletedDate = currentDate
                )
            )

            // Reward +30 points
            val user = userDao.getUserSync()!!
            val pointsAwarded = 30
            val updatedPoints = user.totalPoints + pointsAwarded
            
            userDao.updateUser(user.copy(totalPoints = updatedPoints))

            transactionDao.insertTransaction(
                TransactionEntity(
                    productCode = "WORKOUT_QUEST",
                    productName = "Completed Workout Quest: ${quest.title}",
                    pointsAdded = pointsAwarded,
                    isRedemption = false
                )
            )
        }
    }

    // Medication Reminder Operations
    suspend fun insertMedicationReminder(reminder: MedicationReminderEntity) {
        medicationReminderDao.insertReminder(reminder)
    }

    suspend fun deleteMedicationReminder(reminder: MedicationReminderEntity) {
        medicationReminderDao.deleteReminder(reminder)
    }

    suspend fun takeMedicationReminder(reminderId: Int, currentDate: String) {
        ensureUserExists()
        val reminder = medicationReminderDao.getReminderById(reminderId)
        if (reminder != null && reminder.lastTakenDate != currentDate) {
            medicationReminderDao.updateReminder(
                reminder.copy(
                    isTaken = true,
                    lastTakenDate = currentDate
                )
            )

            // Reward +20 points
            val user = userDao.getUserSync()!!
            val pointsAwarded = 20
            val updatedPoints = user.totalPoints + pointsAwarded

            userDao.updateUser(user.copy(totalPoints = updatedPoints))

            transactionDao.insertTransaction(
                TransactionEntity(
                    productCode = "MEDICATION_REMINDER",
                    productName = "Taken Medication/Vitamin: ${reminder.title}",
                    pointsAdded = pointsAwarded,
                    isRedemption = false
                )
            )
        }
    }

    // Daily Reset Logic
    suspend fun syncQuestsAndRemindersDaily(currentDate: String) {
        ensureUserExists()
        
        // Reset hydration to 0.0 L when a new day is detected
        val user = userDao.getUserSync()
        if (user != null && user.lastHydrationResetDate != currentDate) {
            userDao.updateUser(user.copy(
                currentHydrationAmount = 0.0,
                lastHydrationResetDate = currentDate
            ))
        }
        
        // Fetch all quests to see if we need to reset isCompleted flag
        val quests = workoutQuestDao.getAllQuestsFlow().firstOrNull() ?: emptyList()
        quests.forEach { quest ->
            if (quest.lastCompletedDate != currentDate && quest.isCompleted) {
                workoutQuestDao.updateQuest(quest.copy(isCompleted = false))
            }
        }

        // Fetch all medication reminders to see if we need to reset isTaken flag
        val reminders = medicationReminderDao.getAllRemindersFlow().firstOrNull() ?: emptyList()
        reminders.forEach { reminder ->
            if (reminder.lastTakenDate != currentDate && reminder.isTaken) {
                medicationReminderDao.updateReminder(reminder.copy(isTaken = false))
            }
        }
    }
}
