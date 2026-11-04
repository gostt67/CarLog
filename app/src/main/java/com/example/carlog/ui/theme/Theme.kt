package com.example.carlog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * Палитра Car Log: глубокий синий (дорога, приборная панель) + янтарный акцент (фары, топливо).
 * Светлая тема — тёплый «бумажный» фон, тёмная — графитовый с янтарным акцентом.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF1E3A5F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD7E3F7),
    onPrimaryContainer = Color(0xFF0A1B33),
    secondary = Color(0xFF9A5200),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE0B8),
    onSecondaryContainer = Color(0xFF331A00),
    tertiary = Color(0xFF1F7A5F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCDEEE0),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFF5F2EA),
    onBackground = Color(0xFF171A20),
    surface = Color(0xFFF5F2EA),
    onSurface = Color(0xFF171A20),
    surfaceVariant = Color(0xFFE9E4D8),
    onSurfaceVariant = Color(0xFF4A4D55),
    outline = Color(0xFF7A7D85),
    outlineVariant = Color(0xFFDDD6C8),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFBF8F1),
    surfaceContainerHigh = Color(0xFFEFEADF),
    surfaceContainerHighest = Color(0xFFE9E4D8)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF5A524),
    onPrimary = Color(0xFF2A1800),
    primaryContainer = Color(0xFF5A3A00),
    onPrimaryContainer = Color(0xFFFFDDB0),
    secondary = Color(0xFF9EC1F0),
    onSecondary = Color(0xFF0A2A52),
    secondaryContainer = Color(0xFF254877),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFF6FD3B8),
    onTertiary = Color(0xFF00382C),
    tertiaryContainer = Color(0xFF0F4D3F),
    onTertiaryContainer = Color(0xFFCDEEE0),
    background = Color(0xFF11151C),
    onBackground = Color(0xFFE8E4DA),
    surface = Color(0xFF11151C),
    onSurface = Color(0xFFE8E4DA),
    surfaceVariant = Color(0xFF2A313C),
    onSurfaceVariant = Color(0xFFA9B1BD),
    outline = Color(0xFF6F7886),
    outlineVariant = Color(0xFF333B48),
    surfaceContainerLowest = Color(0xFF0E1218),
    surfaceContainerLow = Color(0xFF1B212B),
    surfaceContainer = Color(0xFF1B212B),
    surfaceContainerHigh = Color(0xFF222A36),
    surfaceContainerHighest = Color(0xFF2A313C)
)

private val BaseTypography = Typography()

private val AppTypography = Typography(
    displaySmall = BaseTypography.displaySmall.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = BaseTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
    titleLarge = BaseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = BaseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = BaseTypography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = BaseTypography.labelLarge.copy(fontWeight = FontWeight.Medium)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun CarLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
