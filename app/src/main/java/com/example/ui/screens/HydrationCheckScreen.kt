package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueTurquoise
import com.example.ui.theme.CitronYellow
import com.example.ui.theme.DeepCurrent
import com.example.ui.viewmodel.EisseryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationCheckScreen(
    viewModel: EisseryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val urineColor by viewModel.urineColorValue.collectAsState()
    val thirstLevel by viewModel.thirstLevelValue.collectAsState()
    val activityLevel by viewModel.activityLevelValue.collectAsState()
    val result by viewModel.assessmentResult.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("hydration_check_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HYDRATION CHECK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = DeepCurrent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("assessment_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepCurrent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "3-Question Assessment 💧",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = DeepCurrent
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Let's perform a quick health check to evaluate your real-time hydration state using simple peer-supported science.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DeepCurrent.copy(alpha = 0.6f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Result Display Block (Animated entrance)
            AnimatedVisibility(
                visible = result != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                result?.let { res ->
                    AssessmentResultCard(
                        status = res,
                        onLogQuickWater = { amount ->
                            viewModel.logWater(amount)
                            viewModel.resetAssessment()
                        },
                        onRetake = { viewModel.resetAssessment() }
                    )
                }
            }

            if (result == null) {
                // Question 1: Urine Color
                QuestionCard(
                    title = "1. Urine Color Check",
                    subtitle = "Compare your latest urinary color against the hydration indicators below.",
                    icon = Icons.Default.ColorLens,
                    iconColor = CitronYellow
                ) {
                    val urineOptions = listOf(
                        Triple("Optimal", "Pale / Clear", Color(0xFFF9F9D6)),
                        Triple("Good", "Straw Yellow", Color(0xFFF4EB8F)),
                        Triple("Mildly Dehydrated", "Bright Yellow", Color(0xFFEDDD40)),
                        Triple("Dehydrated", "Amber Orange", Color(0xFFE0AF1B)),
                        Triple("Severely Dehydrated", "Tea Colored", Color(0xFF9E6500))
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        urineOptions.forEachIndexed { index, option ->
                            val isSelected = urineColor == index
                            Surface(
                                onClick = { viewModel.setUrineColor(index) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("urine_option_$index"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BlueTurquoise.copy(alpha = 0.15f) else Color.White,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueTurquoise) else null,
                                shadowElevation = if (isSelected) 2.dp else 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Visual color reference circle
                                    Surface(
                                        color = option.third,
                                        shape = CircleShape,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                                        modifier = Modifier.size(24.dp)
                                    ) {}

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = option.first,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DeepCurrent
                                            )
                                        )
                                        Text(
                                            text = option.second,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DeepCurrent.copy(alpha = 0.6f)
                                            )
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = BlueTurquoise,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question 2: Thirst Level
                QuestionCard(
                    title = "2. How thirsty do you feel?",
                    subtitle = "Be honest! Listen carefully to your body's request.",
                    icon = Icons.Default.SentimentSatisfiedAlt,
                    iconColor = BlueTurquoise
                ) {
                    val thirstOptions = listOf(
                        "Not thirsty at all (Perfectly fine)",
                        "A little dry (Slight sensation)",
                        "Moderately thirsty (Would love some water)",
                        "Very thirsty / parched (Need a drink ASAP!)"
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        thirstOptions.forEachIndexed { index, option ->
                            val isSelected = thirstLevel == index
                            Surface(
                                onClick = { viewModel.setThirstLevel(index) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("thirst_option_$index"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BlueTurquoise.copy(alpha = 0.15f) else Color.White,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueTurquoise) else null,
                                shadowElevation = if (isSelected) 2.dp else 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = DeepCurrent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = BlueTurquoise,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question 3: Activity Level & Climate
                QuestionCard(
                    title = "3. Physical Activity & Heat",
                    subtitle = "Have you been working out or exposed to sun/heat today?",
                    icon = Icons.Default.WbSunny,
                    iconColor = CitronYellow
                ) {
                    val activityOptions = listOf(
                        "Sedentary / Rest day inside AC room",
                        "Light walking / Normal indoor study",
                        "Moderate activity / Gym workout",
                        "Heavy running / Hot sunny weather outside"
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        activityOptions.forEachIndexed { index, option ->
                            val isSelected = activityLevel == index
                            Surface(
                                onClick = { viewModel.setActivityLevel(index) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("activity_option_$index"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BlueTurquoise.copy(alpha = 0.15f) else Color.White,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueTurquoise) else null,
                                shadowElevation = if (isSelected) 2.dp else 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = DeepCurrent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = BlueTurquoise,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Evaluate Action Button
                Button(
                    onClick = { viewModel.evaluateAssessment() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_assessment_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueTurquoise,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Evaluate Hydration Level",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun QuestionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = iconColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (iconColor == Color.White) DeepCurrent else iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCurrent.copy(alpha = 0.5f)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun AssessmentResultCard(
    status: String,
    onLogQuickWater: (Double) -> Unit,
    onRetake: () -> Unit
) {
    val isHydrated = status == "Hydrated"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .testTag("assessment_result_card"),
        shape = RoundedCornerShape(24.dp),
        color = if (isHydrated) Color(0xFFE8F5E9) else BlueTurquoise.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            if (isHydrated) Color(0xFF81C784) else BlueTurquoise
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = if (isHydrated) Color(0xFF4CAF50) else BlueTurquoise,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isHydrated) Icons.Default.CheckCircle else Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isHydrated) "You are: HYDRATED! 🎉" else "Prompt: TIME TO HYDRATE! 💧",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isHydrated) Color(0xFF2E7D32) else DeepCurrent
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isHydrated) {
                    "Awesome job, buddy! Your body is looking perfectly balanced and active. Keep maintaining your fluid intake throughout the day to support your mental stamina! 🌟"
                } else {
                    "Your body is sending dehydration alerts! Drinking some fresh water right now will immediately increase your energy, keep your skin glowing, and earn you Eisser points! Let's drink together! 🥤"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DeepCurrent.copy(alpha = 0.85f),
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isHydrated) {
                // Actionable buttons
                Text(
                    text = "ACTIONABLE: LOG WATER NOW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BlueTurquoise,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onLogQuickWater(250.0) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueTurquoise),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+250 mL Cup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onLogQuickWater(500.0) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepCurrent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+500 mL Bottle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onRetake,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.7f),
                    contentColor = DeepCurrent
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Text("Retake Assessment", fontWeight = FontWeight.Bold)
            }
        }
    }
}
