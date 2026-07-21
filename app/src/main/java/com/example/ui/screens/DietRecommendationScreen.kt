package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueTurquoise
import com.example.ui.theme.CitronYellow
import com.example.ui.theme.DeepCurrent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietRecommendationScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // State of wizard: 1 for Age, 2 for Height, 3 for Weight, 4 for Result
    var currentStep by remember { mutableIntStateOf(1) }
    
    // User choices (1-based index as strings)
    var selectedAge by remember { mutableStateOf<String?>(null) }
    var selectedHeight by remember { mutableStateOf<String?>(null) }
    var selectedWeight by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("diet_recommendation_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "LETS CHOOSE YOUR MEAL!!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = DeepCurrent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                currentStep--
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag("diet_back_button")
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
            // Step Progress Indicator
            val progressFraction = (currentStep - 1) / 3f
            val animatedProgress by animateFloatAsState(
                targetValue = progressFraction,
                label = "progress_anim"
            )

            if (currentStep < 4) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP $currentStep OF 3",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCurrent.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        )
                        val stepTitle = when (currentStep) {
                            1 -> "Age Profile"
                            2 -> "Height Stats"
                            else -> "Weight Check"
                        }
                        Text(
                            text = stepTitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BlueTurquoise
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .testTag("diet_progress_bar"),
                        color = BlueTurquoise,
                        trackColor = Color(0xFFE9ECEF)
                    )
                }
            }

            // Wizard Step Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when (currentStep) {
                    1 -> {
                        DietQuestionStep(
                            questionText = "How old are you?",
                            subtitleText = "Please select your age range to customize nutritional metabolism factors.",
                            icon = Icons.Default.RestaurantMenu,
                            iconColor = BlueTurquoise,
                            options = listOf(
                                "1" to "12-18 YEARS OLD",
                                "2" to "19-24 YEARS OLD",
                                "3" to "25-45 YEARS OLD",
                                "4" to "45-60 YEARS OLD"
                            ),
                            selectedOption = selectedAge,
                            onOptionSelected = {
                                selectedAge = it
                            },
                            onNext = {
                                if (selectedAge != null) currentStep = 2
                            },
                            nextEnabled = selectedAge != null
                        )
                    }
                    2 -> {
                        DietQuestionStep(
                            questionText = "And how about your tall?",
                            subtitleText = "Your height helps us calculate your general body layout and energy needs.",
                            icon = Icons.Default.Straighten,
                            iconColor = CitronYellow,
                            options = listOf(
                                "1" to "< 150 CM",
                                "2" to "150 - 165 CM",
                                "3" to "166 - 180 CM",
                                "4" to "> 180 CM"
                            ),
                            selectedOption = selectedHeight,
                            onOptionSelected = {
                                selectedHeight = it
                            },
                            onNext = {
                                if (selectedHeight != null) currentStep = 3
                            },
                            nextEnabled = selectedHeight != null
                        )
                    }
                    3 -> {
                        DietQuestionStep(
                            questionText = "Very good!, how about your weight?",
                            subtitleText = "Lastly, select your weight bracket to determine your perfect dietary balance.",
                            icon = Icons.Default.Scale,
                            iconColor = BlueTurquoise,
                            options = listOf(
                                "1" to "< 50 KG",
                                "2" to "50 - 65 KG",
                                "3" to "66 - 80 KG",
                                "4" to "> 80 KG"
                            ),
                            selectedOption = selectedWeight,
                            onOptionSelected = {
                                selectedWeight = it
                            },
                            onNext = {
                                if (selectedWeight != null) currentStep = 4
                            },
                            nextEnabled = selectedWeight != null
                        )
                    }
                    4 -> {
                        // Evaluation calculation based on selected choices
                        val age = selectedAge ?: "1"
                        val height = selectedHeight ?: "1"
                        val weight = selectedWeight ?: "1"

                        val (status, tips) = evaluateDiet(age, height, weight)

                        DietResultView(
                            status = status,
                            tips = tips,
                            onRetake = {
                                selectedAge = null
                                selectedHeight = null
                                selectedWeight = null
                                currentStep = 1
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DietQuestionStep(
    questionText: String,
    subtitleText: String,
    icon: ImageVector,
    iconColor: Color,
    options: List<Pair<String, String>>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step Hero Icon
        Surface(
            color = iconColor.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (iconColor == Color.White) DeepCurrent else iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = questionText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DeepCurrent,
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DeepCurrent.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selection Cards
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (value, label) ->
                val isSelected = selectedOption == value
                Surface(
                    onClick = { onOptionSelected(value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diet_option_$value"),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) BlueTurquoise.copy(alpha = 0.12f) else Color.White,
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BlueTurquoise) else null,
                    shadowElevation = if (isSelected) 2.dp else 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = DeepCurrent
                            )
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = BlueTurquoise,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.Transparent, CircleShape)
                                    .border(2.dp, Color.LightGray, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next Button
        Button(
            onClick = onNext,
            enabled = nextEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("diet_next_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepCurrent,
                contentColor = Color.White,
                disabledContainerColor = DeepCurrent.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Continue",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
fun DietResultView(
    status: String,
    tips: String,
    onRetake: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero decoration
        Surface(
            color = CitronYellow.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🥗", fontSize = 36.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "RECOMMENDED DIET PLAN",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = BlueTurquoise,
                letterSpacing = 1.5.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Meal Diet Results",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DeepCurrent,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Result Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("diet_result_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Physical Status Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = BlueTurquoise,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "STATUS FISIK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = status,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = DeepCurrent
                    ),
                    modifier = Modifier.testTag("diet_status_text")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Tips Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = CitronYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REKOMENDASI DIET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tips,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCurrent.copy(alpha = 0.8f),
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.testTag("diet_tips_text")
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Retake Button
        Button(
            onClick = onRetake,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("diet_reset_button"),
            colors = ButtonDefaults.buttonColors(containerColor = DeepCurrent),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Plan Another Meal", fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Pure evaluation logic corresponding directly to the combinations defined by the user.
 */
private fun evaluateDiet(pilihUsia: String, pilihTinggi: String, pilihBerat: String): Pair<String, String> {
    // Logika Kombinasi 1: Usia Remaja, Tinggi Normal, Berat Kurang
    if (pilihUsia == "1" && (pilihTinggi == "2" || pilihTinggi == "3") && pilihBerat == "1") {
        return "Berat Badan Kurang (Underweight) untuk Usia Pertumbuhan" to
                "Fokus pada surplus kalori sehat. Perbanyak protein (telur, ayam, tahu) dan karbohidrat kompleks (nasi merah, kentang) untuk mendukung pertumbuhan tulang dan otot."
    }
    // Logika Kombinasi 2: Usia Produktif, Tinggi Ideal, Berat Ideal
    else if ((pilihUsia == "2" || pilihUsia == "3") && pilihTinggi == "2" && pilihBerat == "2") {
        return "Berat Badan Normal (Ideal)" to
                "Pertahankan pola makan gizi seimbang (4 sehat 5 sempurna). Jaga asupan air putih 2 liter sehari dan kombinasikan dengan olahraga kardio ringan 3 kali seminggu."
    }
    // Logika Kombinasi 3: Usia Produktif, Tinggi Pendek/Sedang, Berat Berlebih
    else if ((pilihUsia == "2" || pilihUsia == "3") && (pilihTinggi == "1" || pilihTinggi == "2") && (pilihBerat == "3" || pilihBerat == "4")) {
        return "Kelebihan Berat Badan (Overweight)" to
                "Lakukan defisit kalori ringan. Kurangi makanan berminyak (gorengan) dan tinggi gula. Perbanyak porsi sayuran hijau dan buah-buahan sebagai camilan sehat."
    }
    // Logika Kombinasi 4: Usia Dewasa Akhir/Lansia, Berat Berlebih
    else if (pilihUsia == "4" && (pilihBerat == "3" || pilihBerat == "4")) {
        return "Kelebihan Berat Badan di Usia Dewasa" to
                "Fokus pada makanan rendah kolesterol dan rendah garam untuk menjaga kesehatan jantung. Perbanyak konsumsi serat dari gandum utuh dan kurangi porsi karbohidrat sederhana."
    }
    // Default
    else {
        return "Kategori Umum / Seimbang" to
                "Gunakan rumus piring makan sehat: 1/2 piring sayur dan buah, 1/4 piring protein, dan 1/4 piring karbohidrat. Batasi konsumsi makanan cepat saji."
    }
}
