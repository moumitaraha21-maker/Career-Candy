package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = CandyPrimary,
  onPrimary = Color.White,
  primaryContainer = CandyPrimaryContainer,
  onPrimaryContainer = CandyOnPrimaryContainer,
  secondary = CandySecondary,
  onSecondary = Color.White,
  secondaryContainer = CandySecondaryContainer,
  onSecondaryContainer = CandyOnSecondaryContainer,
  tertiary = CandyTertiary,
  onTertiary = Color.White,
  tertiaryContainer = CandyTertiaryContainer,
  onTertiaryContainer = CandyOnTertiaryContainer,
  background = CandyBackground,
  onBackground = CandyOnBackground,
  surface = CandySurface,
  onSurface = CandyOnSurface,
  surfaceVariant = CandySurfaceVariant,
  onSurfaceVariant = CandyOnBackground,
  outline = CandyOutline,
  outlineVariant = CandyOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
  primary = CandyPrimary,
  onPrimary = Color.Black,
  primaryContainer = CandyPrimaryContainer,
  onPrimaryContainer = CandyOnPrimaryContainer,
  secondary = CandySecondary,
  onSecondary = Color.Black,
  secondaryContainer = CandySecondaryContainer,
  onSecondaryContainer = CandyOnSecondaryContainer,
  tertiary = CandyTertiary,
  onTertiary = Color.Black,
  tertiaryContainer = CandyTertiaryContainer,
  onTertiaryContainer = CandyOnTertiaryContainer,
  background = Color(0xFF191218), // Midnight plum dark canvas
  onBackground = Color(0xFFFEF7FF),
  surface = Color(0xFF261D25),
  onSurface = Color(0xFFFEF7FF),
  surfaceVariant = Color(0xFF332632),
  onSurfaceVariant = Color(0xFFFEF7FF),
  outline = CandyOutline,
  outlineVariant = CandyOutlineVariant
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // For Career Candy, we prefer our custom sugar-rich brand palette over general system dynamic colors
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
