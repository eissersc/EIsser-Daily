package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface HydrationLogDao {
    @Query("SELECT * FROM hydration_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<HydrationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HydrationLogEntity)

    @Delete
    suspend fun deleteLog(log: HydrationLogEntity)

    @Query("DELETE FROM hydration_logs")
    suspend fun clearAllLogs()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface WorkoutQuestDao {
    @Query("SELECT * FROM workout_quests ORDER BY scheduledTime ASC")
    fun getAllQuestsFlow(): Flow<List<WorkoutQuestEntity>>

    @Query("SELECT * FROM workout_quests WHERE id = :id LIMIT 1")
    suspend fun getQuestById(id: Int): WorkoutQuestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: WorkoutQuestEntity)

    @Update
    suspend fun updateQuest(quest: WorkoutQuestEntity)

    @Delete
    suspend fun deleteQuest(quest: WorkoutQuestEntity)
}

@Dao
interface MedicationReminderDao {
    @Query("SELECT * FROM medication_reminders ORDER BY scheduledTime ASC")
    fun getAllRemindersFlow(): Flow<List<MedicationReminderEntity>>

    @Query("SELECT * FROM medication_reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Int): MedicationReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MedicationReminderEntity)

    @Update
    suspend fun updateReminder(reminder: MedicationReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: MedicationReminderEntity)
}
