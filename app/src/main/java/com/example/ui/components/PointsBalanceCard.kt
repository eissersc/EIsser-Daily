package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CitronYellow
import com.example.ui.theme.DeepCurrent

@Composable
fun PointsBalanceCard(
    points: Int,
    onCatalogClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pop animation on points change
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PointsScale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(animatedScale)
            .testTag("points_balance_card"),
        shape = RoundedCornerShape(24.dp),
        color = CitronYellow,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "YOUR BALANCE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCurrent.copy(alpha = 0.6f),
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Points Icon",
                            tint = DeepCurrent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$points pts",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = DeepCurrent,
                                fontSize = 34.sp
                            ),
                            modifier = Modifier.testTag("points_text")
                        )
                    }
                }

                // Decorative product tag
                Surface(
                    color = DeepCurrent,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        text = "WELLNESS LOYALTY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CitronYellow,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Track your wellness! Get points by logging water, finishing your workout quests, or taking medications/vitamins. Trade your points for amazing rewards! 🎁",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DeepCurrent.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rewards Catalog Button (Filled primary look)
            Button(
                onClick = onCatalogClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("dashboard_rewards_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepCurrent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Browse & Redeem Rewards Catalog",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
