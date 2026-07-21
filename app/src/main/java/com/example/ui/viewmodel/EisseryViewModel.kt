package com.example.ui.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.HydrationLogEntity
import com.example.data.database.TransactionEntity
import com.example.data.database.UserEntity
import com.example.data.database.WorkoutQuestEntity
import com.example.data.database.MedicationReminderEntity
import com.example.data.repository.EisseryRepository
import com.example.receiver.HydrationReminderReceiver
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EisseryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = EisseryRepository(
        database.userDao(),
        database.hydrationLogDao(),
        database.transactionDao(),
        database.workoutQuestDao(),
        database.medicationReminderDao()
    )

    val user: StateFlow<UserEntity?> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val hydrationLogs: StateFlow<List<HydrationLogEntity>> = repository.allLogsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactionsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val workoutQuests: StateFlow<List<WorkoutQuestEntity>> = repository.allQuestsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val medicationReminders: StateFlow<List<MedicationReminderEntity>> = repository.allRemindersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Assessment States
    private val _urineColorValue = MutableStateFlow(0) // 0 to 4
    val urineColorValue: StateFlow<Int> = _urineColorValue.asStateFlow()

    private val _thirstLevelValue = MutableStateFlow(0) // 0 to 4
    val thirstLevelValue: StateFlow<Int> = _thirstLevelValue.asStateFlow()

    private val _activityLevelValue = MutableStateFlow(0) // 0 to 4
    val activityLevelValue: StateFlow<Int> = _activityLevelValue.asStateFlow()

    private val _assessmentResult = MutableStateFlow<String?>(null) // "Hydrated" or "Time to Hydrate"
    val assessmentResult: StateFlow<String?> = _assessmentResult.asStateFlow()

    // Status Messages (e.g., point redemption success or errors)
    private val _statusMessage = MutableSharedFlow<String>()
    val statusMessage: SharedFlow<String> = _statusMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserExists()
            repository.syncQuestsAndRemindersDaily(getCurrentDateString())
            
            // Populate defaults on fresh install if empty
            populateDefaultQuestsAndRemindersIfNeeded()
        }
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private suspend fun populateDefaultQuestsAndRemindersIfNeeded() {
        // Do not insert pre-populated quests; let the user add them themselves as requested
    }

    // Hydration log operations
    fun logWater(amountMl: Double) {
        viewModelScope.launch {
            repository.logWater(amountMl)
            _statusMessage.emit("Logged ${amountMl.toInt()} mL water. Gained +10 Loyalty Points!")
        }
    }

    fun deleteLog(log: HydrationLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
            _statusMessage.emit("Deleted water log of ${log.amount.toInt()} mL.")
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearLogs()
            _statusMessage.emit("All hydration logs cleared.")
        }
    }

    fun updateGoal(newGoalMl: Double) {
        viewModelScope.launch {
            repository.updateDailyGoal(newGoalMl)
            _statusMessage.emit("Daily hydration goal updated to ${newGoalMl.toInt()} mL!")
        }
    }

    fun updateProfilePicture(uriString: String?) {
        viewModelScope.launch {
            var finalUriString = uriString
            if (uriString != null && uriString.startsWith("content://")) {
                try {
                    val context = getApplication<Application>().applicationContext
                    val inputUri = android.net.Uri.parse(uriString)
                    context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                        val outputFile = java.io.File(context.filesDir, "custom_profile_picture.jpg")
                        outputFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        finalUriString = outputFile.absolutePath
                    }
                } catch (e: Exception) {
                    Log.e("EisseryViewModel", "Failed to copy profile picture to internal storage", e)
                }
            }
            repository.updateProfilePicture(finalUriString)
            _statusMessage.emit("Profile picture updated successfully!")
        }
    }

    fun updateProfileName(name: String) {
        viewModelScope.launch {
            repository.updateProfileName(name)
            _statusMessage.emit("Profile name updated to $name!")
        }
    }

    // Redeem Reward Catalog Item
    fun redeemRewardItem(rewardName: String, pointsCost: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.redeemReward(rewardName, pointsCost)
            result.onSuccess { transaction ->
                onSuccess()
                _statusMessage.emit("Claimed '$rewardName'! Deduced $pointsCost points.")
            }.onFailure { exception ->
                _statusMessage.emit(exception.message ?: "Failed to claim reward.")
            }
        }
    }

    // Set Assessment Answers
    fun setUrineColor(value: Int) {
        _urineColorValue.value = value
    }

    fun setThirstLevel(value: Int) {
        _thirstLevelValue.value = value
    }

    fun setActivityLevel(value: Int) {
        _activityLevelValue.value = value
    }

    // Evaluate three-question assessment using conditional logic
    fun evaluateAssessment() {
        val urine = _urineColorValue.value
        val thirst = _thirstLevelValue.value
        val activity = _activityLevelValue.value

        val needsHydration = (urine >= 2) || (thirst >= 2) || (activity >= 3 && thirst >= 1)

        val resultStr = if (needsHydration) "Time to Hydrate" else "Hydrated"
        _assessmentResult.value = resultStr
        
        viewModelScope.launch {
            repository.rewardHydrationAssessment(resultStr)
            _statusMessage.emit("Assessment complete! Gained +15 Loyalty Points!")
        }
    }

    fun resetAssessment() {
        _urineColorValue.value = 0
        _thirstLevelValue.value = 0
        _activityLevelValue.value = 0
        _assessmentResult.value = null
    }

    // Workout Quest Operations
    fun addWorkoutQuest(title: String, category: String, scheduledTime: String) {
        viewModelScope.launch {
            repository.insertWorkoutQuest(
                WorkoutQuestEntity(
                    title = title,
                    category = category,
                    scheduledTime = scheduledTime,
                    isCompleted = false
                )
            )
            _statusMessage.emit("Added daily workout quest: $title at $scheduledTime")
        }
    }

    fun deleteWorkoutQuest(quest: WorkoutQuestEntity) {
        viewModelScope.launch {
            repository.deleteWorkoutQuest(quest)
            _statusMessage.emit("Deleted workout quest: ${quest.title}")
        }
    }

    fun completeWorkoutQuest(questId: Int) {
        viewModelScope.launch {
            repository.completeWorkoutQuest(questId, getCurrentDateString())
            _statusMessage.emit("Completed workout quest! Gained +30 Loyalty Points!")
        }
    }

    // Medication Reminder Operations
    fun addMedicationReminder(title: String, scheduledTime: String, dosage: String) {
        viewModelScope.launch {
            repository.insertMedicationReminder(
                MedicationReminderEntity(
                    title = title,
                    scheduledTime = scheduledTime,
                    dosage = dosage,
                    isTaken = false
                )
            )
            _statusMessage.emit("Added medication/vitamin reminder: $title ($dosage)")
        }
    }

    fun deleteMedicationReminder(reminder: MedicationReminderEntity) {
        viewModelScope.launch {
            repository.deleteMedicationReminder(reminder)
            _statusMessage.emit("Deleted reminder: ${reminder.title}")
        }
    }

    fun takeMedicationReminder(reminderId: Int) {
        viewModelScope.launch {
            repository.takeMedicationReminder(reminderId, getCurrentDateString())
            _statusMessage.emit("Took medication/vitamin! Gained +20 Loyalty Points!")
        }
    }

    // Alarm/Notifications Scheduler (Stub or active depending on system preference)
    fun scheduleLocalReminder(context: Context, intervalMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = SystemClock.elapsedRealtime() + (intervalMinutes * 60 * 1000)

        try {
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
            viewModelScope.launch {
                _statusMessage.emit("Friendly reminder scheduled for $intervalMinutes minutes from now!")
            }
        } catch (e: Exception) {
            Log.e("EisseryViewModel", "Failed to schedule alarm", e)
        }
    }

    fun triggerInstantNotification(context: Context) {
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        context.sendBroadcast(intent)
        viewModelScope.launch {
            _statusMessage.emit("Supportive peer reminder triggered!")
        }
    }
}
