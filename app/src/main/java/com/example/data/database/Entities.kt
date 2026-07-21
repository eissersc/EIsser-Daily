package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Eisser Student",
    val currentHydrationAmount: Double = 0.0,
    val dailyHydrationGoal: Double = 2000.0,
    val totalPoints: Int = 0,
    val profilePictureUri: String? = null,
    val lastHydrationResetDate: String = ""
)

@Entity(tableName = "hydration_logs")
data class HydrationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productCode: String,
    val productName: String,
    val pointsAdded: Int, // positive for codes, negative for reward redemptions
    val timestamp: Long = System.currentTimeMillis(),
    val isRedemption: Boolean = false
)

@Entity(tableName = "workout_quests")
data class WorkoutQuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val scheduledTime: String,
    val isCompleted: Boolean = false,
    val lastCompletedDate: String = ""
)

@Entity(tableName = "medication_reminders")
data class MedicationReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val scheduledTime: String,
    val dosage: String,
    val isTaken: Boolean = false,
    val lastTakenDate: String = ""
)
