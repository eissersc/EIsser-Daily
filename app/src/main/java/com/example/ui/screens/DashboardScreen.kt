package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.HorizontalDivider
import com.example.data.database.HydrationLogEntity
import com.example.data.database.TransactionEntity
import com.example.ui.components.HydrationProgressRing
import com.example.ui.components.PointsBalanceCard
import com.example.ui.theme.BlueTurquoise
import com.example.ui.theme.CitronYellow
import com.example.ui.theme.DeepCurrent
import com.example.ui.viewmodel.EisseryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: EisseryViewModel,
    onNavigateToAssessment: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToDiet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val hydrationLogs by viewModel.hydrationLogs.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val workoutQuests by viewModel.workoutQuests.collectAsState()
    val medicationReminders by viewModel.medicationReminders.collectAsState()
    val recentTransactions = remember(transactions) { transactions.take(3) }

    var showReminderDialog by remember { mutableStateOf(false) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var profileNameInput by remember { mutableStateOf(user?.name ?: "Eisser Student") }

    LaunchedEffect(user) {
        user?.let {
            profileNameInput = it.name
        }
    }

    // Dialog state for adding Workout Quests
    var showAddQuestDialog by remember { mutableStateOf(false) }
    var questTitleInput by remember { mutableStateOf("") }
    var questCategoryInput by remember { mutableStateOf("Cardio") }
    var questTimeInput by remember { mutableStateOf("07:00") }

    // Dialog state for adding Medications
    var showAddMedicationDialog by remember { mutableStateOf(false) }
    var medTitleInput by remember { mutableStateOf("") }
    var medDosageInput by remember { mutableStateOf("1 Tablet") }
    var medTimeInput by remember { mutableStateOf("08:00") }

    // Request Notification permission for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (showAddQuestDialog) {
        AlertDialog(
            onDismissRequest = { showAddQuestDialog = false },
            title = { Text("Add Daily Workout Quest", fontWeight = FontWeight.Bold, color = DeepCurrent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = questTitleInput,
                        onValueChange = { questTitleInput = it },
                        label = { Text("Quest/Workout Title") },
                        placeholder = { Text("e.g. Morning Jog, Cardio HIIT") },
                        modifier = Modifier.fillMaxWidth().testTag("quest_title_input")
                    )

                    Text("Category", style = MaterialTheme.typography.titleSmall, color = DeepCurrent)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = listOf("Cardio", "Strength", "Yoga", "Flexibility")
                        categories.forEach { cat ->
                            val isSelected = questCategoryInput == cat
                            Surface(
                                onClick = { questCategoryInput = cat },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) BlueTurquoise.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BlueTurquoise) else null
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                                    Text(cat, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DeepCurrent)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = questTimeInput,
                        onValueChange = { questTimeInput = it },
                        label = { Text("Schedule Time (e.g. 07:00)") },
                        placeholder = { Text("07:00") },
                        modifier = Modifier.fillMaxWidth().testTag("quest_time_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (questTitleInput.isNotBlank()) {
                            viewModel.addWorkoutQuest(
                                title = questTitleInput.trim(),
                                category = questCategoryInput,
                                scheduledTime = questTimeInput.trim()
                            )
                            showAddQuestDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_add_quest_btn")
                ) {
                    Text("Add Quest", fontWeight = FontWeight.Bold, color = BlueTurquoise)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (showAddMedicationDialog) {
        AlertDialog(
            onDismissRequest = { showAddMedicationDialog = false },
            title = { Text("Add Medicine/Vitamin Reminder", fontWeight = FontWeight.Bold, color = DeepCurrent) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = medTitleInput,
                        onValueChange = { medTitleInput = it },
                        label = { Text("Medicine or Vitamin Name") },
                        placeholder = { Text("e.g. Vitamin C, Daily Omega-3") },
                        modifier = Modifier.fillMaxWidth().testTag("med_title_input")
                    )

                    OutlinedTextField(
                        value = medDosageInput,
                        onValueChange = { medDosageInput = it },
                        label = { Text("Dosage / Instructions") },
                        placeholder = { Text("e.g. 1 Tablet, 2 Drops") },
                        modifier = Modifier.fillMaxWidth().testTag("med_dosage_input")
                    )

                    OutlinedTextField(
                        value = medTimeInput,
                        onValueChange = { medTimeInput = it },
                        label = { Text("Daily Reminder Time (e.g. 08:00)") },
                        placeholder = { Text("08:00") },
                        modifier = Modifier.fillMaxWidth().testTag("med_time_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (medTitleInput.isNotBlank()) {
                            viewModel.addMedicationReminder(
                                title = medTitleInput.trim(),
                                scheduledTime = medTimeInput.trim(),
                                dosage = medDosageInput.trim()
                            )
                            showAddMedicationDialog = false
                        }
                    },
                    modifier = Modifier.testTag("submit_add_med_btn")
                ) {
                    Text("Configure Reminder", fontWeight = FontWeight.Bold, color = CitronYellow)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMedicationDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("dashboard_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Title Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HELLO, ${user?.name?.uppercase() ?: "STUDENT"}!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.6f),
                            letterSpacing = 1.5.sp
                        )
                    )
                    Text(
                        text = "Eisser Daily Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent,
                            fontSize = 22.sp
                        )
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { showProfileDialog = true }
                        .testTag("profile_picture_tap"),
                    shape = CircleShape,
                    color = DeepCurrent
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        val currentUri = user?.profilePictureUri
                        if (currentUri == null) {
                            Text(
                                text = "👤",
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        } else if (currentUri == "preset:orca") {
                            AsyncImage(
                                model = R.drawable.img_orca_logo_no_water_1784464127960,
                                contentDescription = "Orca Logo Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (currentUri.startsWith("preset:")) {
                            val emoji = when (currentUri) {
                                "preset:droplet" -> "💧"
                                "preset:workout" -> "🏋️"
                                "preset:diet" -> "🥗"
                                "preset:scholar" -> "🎓"
                                "preset:star" -> "⭐"
                                else -> "👤"
                            }
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        } else {
                            AsyncImage(
                                model = currentUri,
                                contentDescription = "User Custom Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // 1. Hydration Progress Ring
        item {
            user?.let {
                HydrationProgressRing(
                    current = it.currentHydrationAmount,
                    goal = it.dailyHydrationGoal
                )
            }
        }

        // 2. Points Balance Card
        item {
            user?.let {
                PointsBalanceCard(
                    points = it.totalPoints,
                    onCatalogClick = onNavigateToCatalog
                )
            }
        }

        // Quick log buttons row
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK LOG WATER (+10 PTS)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickAddButton(
                            label = "+250 mL",
                            subLabel = "Cup",
                            color = BlueTurquoise,
                            onClick = { viewModel.logWater(250.0) },
                            modifier = Modifier.weight(1f).testTag("quick_add_250")
                        )
                        QuickAddButton(
                            label = "+500 mL",
                            subLabel = "Bottle",
                            color = BlueTurquoise,
                            onClick = { viewModel.logWater(500.0) },
                            modifier = Modifier.weight(1f).testTag("quick_add_500")
                        )
                        QuickAddButton(
                            label = "+750 mL",
                            subLabel = "Thermos",
                            color = BlueTurquoise,
                            onClick = { viewModel.logWater(750.0) },
                            modifier = Modifier.weight(1f).testTag("quick_add_750")
                        )
                    }
                }
            }
        }

        // Quick Actions Grid (Perfectly balanced 2-column actions grid)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Action 1: Hydration Check
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable(onClick = onNavigateToAssessment)
                        .testTag("assessment_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = BlueTurquoise,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Check Hydration (+15 pts)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCurrent,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Action 2: Diet Meal Recommendation
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable(onClick = onNavigateToDiet)
                        .testTag("diet_recommendation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = CitronYellow,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choose Meal",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCurrent,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Daily Workout Quests
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = DeepCurrent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY WORKOUT QUESTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                    )
                }

                IconButton(
                    onClick = {
                        questTitleInput = ""
                        questCategoryInput = "Cardio"
                        questTimeInput = "07:00"
                        showAddQuestDialog = true
                    },
                    modifier = Modifier.size(24.dp).testTag("add_workout_quest_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Workout Quest",
                        tint = BlueTurquoise
                    )
                }
            }
        }

        if (workoutQuests.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No workout quests configured.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCurrent.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+' to add your daily workout quest! 🏋️‍♂️",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        } else {
            items(workoutQuests, key = { it.id }) { quest ->
                WorkoutQuestItem(
                    quest = quest,
                    onComplete = { viewModel.completeWorkoutQuest(quest.id) },
                    onDelete = { viewModel.deleteWorkoutQuest(quest) }
                )
            }
        }

        // Medication & Vitamin Reminders (Only shown/needed for those who explicitly configure it)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = DeepCurrent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MEDICATION & VITAMINS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                    )
                }

                IconButton(
                    onClick = {
                        medTitleInput = ""
                        medDosageInput = "1 Tablet"
                        medTimeInput = "08:00"
                        showAddMedicationDialog = true
                    },
                    modifier = Modifier.size(24.dp).testTag("add_medication_reminder_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Medication Reminder",
                        tint = CitronYellow
                    )
                }
            }
        }

        if (medicationReminders.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active reminders.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCurrent.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Configure only if you need medication or vitamin tracking. Feel free to leave empty if not needed! 💊",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        } else {
            items(medicationReminders, key = { it.id }) { reminder ->
                MedicationReminderItem(
                    reminder = reminder,
                    onTake = { viewModel.takeMedicationReminder(reminder.id) },
                    onDelete = { viewModel.deleteMedicationReminder(reminder) }
                )
            }
        }

        // Notification Setup (Standard test reminders)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = DeepCurrent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Friendly Notifications",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCurrent
                                )
                            )
                        }

                        IconButton(
                            onClick = { showReminderDialog = !showReminderDialog },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Reminder Info",
                                tint = DeepCurrent.copy(alpha = 0.6f)
                            )
                        }
                    }

                    if (showReminderDialog) {
                        Surface(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Reminders are sent in a supportive peer-to-peer tone to keep your healthy habits active, helping you maintain a high quality of life effortlessly.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCurrent.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.scheduleLocalReminder(context, 120) },
                            modifier = Modifier.weight(1f).testTag("schedule_2h_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepCurrent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set 2h Alarm", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.triggerInstantNotification(context) },
                            modifier = Modifier.weight(1f).testTag("trigger_instant_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlueTurquoise,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Reminder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Recent hydration logs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S WATER LOGS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                )

                if (hydrationLogs.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.Red.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { viewModel.clearAllLogs() }
                            .padding(4.dp)
                    )
                }
            }
        }

        if (hydrationLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No water logs today.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCurrent.copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            text = "Drink up and log your first cup! 💧",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        } else {
            items(hydrationLogs, key = { it.id }) { log ->
                WaterLogItem(log = log, onDelete = { viewModel.deleteLog(log) })
            }
        }

        // Recent Transactions Space
        if (transactions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RECENT TRANSACTIONS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            items(recentTransactions, key = { it.id }) { transaction ->
                TransactionListItem(transaction = transaction)
            }
        }

        // Footer gap
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showProfileDialog) {
        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                viewModel.updateProfilePicture(uri.toString())
            }
        }

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = "Edit Profile & Avatar",
                    fontWeight = FontWeight.Bold,
                    color = DeepCurrent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Preview
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = DeepCurrent.copy(alpha = 0.9f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                val currentUri = user?.profilePictureUri
                                if (currentUri == null) {
                                    Text("👤", fontSize = 42.sp, color = Color.White)
                                } else if (currentUri == "preset:orca") {
                                    AsyncImage(
                                        model = R.drawable.img_orca_logo_no_water_1784464127960,
                                        contentDescription = "Preview Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (currentUri.startsWith("preset:")) {
                                    val emoji = when (currentUri) {
                                        "preset:droplet" -> "💧"
                                        "preset:workout" -> "🏋️"
                                        "preset:diet" -> "🥗"
                                        "preset:scholar" -> "🎓"
                                        "preset:star" -> "⭐"
                                        else -> "👤"
                                    }
                                    Text(emoji, fontSize = 42.sp, color = Color.White)
                                } else {
                                    AsyncImage(
                                        model = currentUri,
                                        contentDescription = "Preview Custom",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        
                        // Edit overlay circle
                        Surface(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            color = BlueTurquoise,
                            tonalElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Upload Custom Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.testTag("upload_custom_avatar_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = BlueTurquoise)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload custom photo", color = BlueTurquoise, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)

                    // Presets selector
                    Text(
                        text = "Or choose a standard preset avatar",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepCurrent.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )

                    // Lay them out in a beautiful grid or Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        val presetList = listOf(
                            Pair("preset:orca", "🐳"),
                            Pair("preset:droplet", "💧"),
                            Pair("preset:workout", "🏋️"),
                            Pair("preset:diet", "🥗"),
                            Pair("preset:scholar", "🎓"),
                            Pair("preset:star", "⭐")
                        )
                        presetList.forEach { (presetKey, displayValue) ->
                            val isSelected = user?.profilePictureUri == presetKey
                            Surface(
                                onClick = { viewModel.updateProfilePicture(presetKey) },
                                modifier = Modifier.size(38.dp),
                                shape = CircleShape,
                                color = if (isSelected) BlueTurquoise.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.15f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueTurquoise) else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (presetKey == "preset:orca") {
                                        AsyncImage(
                                            model = R.drawable.img_orca_logo_no_water_1784464127960,
                                            contentDescription = "Orca Preset",
                                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(displayValue, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = profileNameInput,
                        onValueChange = { profileNameInput = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("Enter your name") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (profileNameInput.isNotBlank()) {
                            viewModel.updateProfileName(profileNameInput.trim())
                        }
                        showProfileDialog = false
                    },
                    modifier = Modifier.testTag("save_profile_btn")
                ) {
                    Text("Save & Close", fontWeight = FontWeight.Bold, color = BlueTurquoise)
                }
            }
        )
    }
}

@Composable
fun QuickAddButton(
    label: String,
    subLabel: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = DeepCurrent.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun WaterLogItem(
    log: HydrationLogEntity,
    onDelete: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = formatter.format(Date(log.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BlueTurquoise.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = BlueTurquoise,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${log.amount.toInt()} mL",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent
                        )
                    )
                    Text(
                        text = "Logged at $timeString",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCurrent.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Log",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun TransactionListItem(
    transaction: TransactionEntity
) {
    val formatter = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
    val timeString = formatter.format(Date(transaction.timestamp))
    val isRedeem = transaction.isRedemption

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.productName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCurrent
                    )
                )
                Text(
                    text = "Category: ${transaction.productCode} • $timeString",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DeepCurrent.copy(alpha = 0.5f)
                    )
                )
            }

            Text(
                text = if (isRedeem) "${transaction.pointsAdded} pts" else "+${transaction.pointsAdded} pts",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isRedeem) Color.Red.copy(alpha = 0.8f) else BlueTurquoise
                )
            )
        }
    }
}

@Composable
fun WorkoutQuestItem(
    quest: com.example.data.database.WorkoutQuestEntity,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val isCompleted = quest.isCompleted
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (isCompleted) BlueTurquoise.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface,
        border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, BlueTurquoise.copy(alpha = 0.3f)) else null,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isCompleted) BlueTurquoise.copy(alpha = 0.15f) else DeepCurrent.copy(alpha = 0.08f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = if (isCompleted) BlueTurquoise else DeepCurrent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent,
                            textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = DeepCurrent.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = quest.category.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepCurrent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = DeepCurrent.copy(alpha = 0.5f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Daily at ${quest.scheduledTime}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isCompleted) {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueTurquoise,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("complete_quest_${quest.id}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        color = BlueTurquoise.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = BlueTurquoise,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Earned", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BlueTurquoise)
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_quest_${quest.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove Quest",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationReminderItem(
    reminder: com.example.data.database.MedicationReminderEntity,
    onTake: () -> Unit,
    onDelete: () -> Unit
) {
    val isTaken = reminder.isTaken
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (isTaken) CitronYellow.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (isTaken) androidx.compose.foundation.BorderStroke(1.dp, CitronYellow.copy(alpha = 0.4f)) else null,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isTaken) CitronYellow.copy(alpha = 0.25f) else DeepCurrent.copy(alpha = 0.08f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = DeepCurrent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent,
                            textDecoration = if (isTaken) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = CitronYellow.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = reminder.dosage.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepCurrent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time",
                            tint = DeepCurrent.copy(alpha = 0.5f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Take daily at ${reminder.scheduledTime}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCurrent.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isTaken) {
                    Button(
                        onClick = onTake,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepCurrent,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("take_medication_${reminder.id}"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Take", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        color = CitronYellow.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Taken",
                                tint = DeepCurrent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Taken", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DeepCurrent)
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_medication_${reminder.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove Reminder",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
