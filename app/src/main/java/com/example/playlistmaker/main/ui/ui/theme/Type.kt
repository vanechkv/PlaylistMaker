package com.example.playlistmaker.main.ui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium))
val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular))


val AppTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontSize = 16.sp
    ),
    displaySmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontSize = 12.sp
    ),
    bodySmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontSize = 11.sp
    ),
    titleLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontSize = 19.sp
    ),
    titleSmall = TextStyle(
        fontFamily = YsDisplayMedium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = YsDisplayRegular,
        fontSize = 13.sp
    )
)