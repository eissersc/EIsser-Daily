package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BlueTurquoise,
    secondary = CitronYellow,
    tertiary = DeepCurrent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DeepCurrent,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color.LightGray
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BlueTurquoise,
    secondary = CitronYellow,
    tertiary = DeepCurrent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = DeepCurrent,
    onBackground = DeepCurrent,
    onSurface = DeepCurrent,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = DeepCurrent
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // For branded apps, disable dynamicColor by default to guarantee brand consistency
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
