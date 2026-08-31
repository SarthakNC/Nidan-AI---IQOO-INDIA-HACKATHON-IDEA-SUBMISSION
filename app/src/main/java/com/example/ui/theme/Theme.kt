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
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealPrimaryContainer,
    secondary = SlateSecondaryDark,
    onSecondary = SlateOnSecondary,
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = SlateSecondaryContainer,
    tertiary = IndigoTertiaryContainer,
    onTertiary = IndigoOnTertiaryContainer,
    background = BackgroundDark,
    onBackground = Color(0xFFE1E3DF),
    surface = SurfaceDark,
    onSurface = Color(0xFFE1E3DF),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC0C8C6),
    outline = ClinicalOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = SlateSecondary,
    onSecondary = SlateOnSecondary,
    secondaryContainer = SlateSecondaryContainer,
    onSecondaryContainer = SlateOnSecondaryContainer,
    tertiary = IndigoTertiary,
    onTertiary = IndigoOnTertiary,
    tertiaryContainer = IndigoTertiaryContainer,
    onTertiaryContainer = IndigoOnTertiaryContainer,
    background = ClinicalBackground,
    onBackground = ClinicalOnBackground,
    surface = ClinicalSurface,
    onSurface = ClinicalOnSurface,
    surfaceVariant = ClinicalSurfaceVariant,
    onSurfaceVariant = ClinicalOnSurfaceVariant,
    outline = ClinicalOutline,
    outlineVariant = ClinicalOutlineVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Enforce high-contrast clinical field UI
  dynamicColor: Boolean = false, // Keep consistent clinical branding for field work
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


