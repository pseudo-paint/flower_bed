package com.paint.flowerbed

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

object Theme {
    var dark_theme : Boolean = false
    private val BG_COLOR_LIGHT : Color = Color(0xFFDEDEDE)
    private val BG_COLOR_DARK : Color = Color(0xFFFF00FF)
    private val TEXT_COLOR_LIGHT : Color = Color(0xFF1B025D)
    private val TEXT_COLOR_DARK : Color = Color(0xFFFFFFFF)
    private val OVERLAY_COLOR_LIGHT : Color = Color(0xFFCCCBC0)
    private val OVERLAY_COLOR_DARK : Color = Color(0xFFCCCBC0)
    private val ACCENT_COLOR_LIGHT : Color = Color(0xFFB8B4A9)
    private val ACCENT_COLOR_DARK : Color = Color(0xFFB8B4A9)
    private val ACCENT_DEEP_COLOR_LIGHT: Color = Color(0xFF7F7969)
    private val ACCENT_DEEP_COLOR_DARK: Color = Color(0xFF7F7969)
    private val CONFIRM_COLOR_LIGHT : Color = Color(0xFF3A45D5)
    private val CONFIRM_COLOR_DARK : Color = Color(0xFF3A45D5)
    private val REJECT_COLOR_LIGHT : Color = Color(0xFFDE2D42)
    private val REJECT_COLOR_DARK : Color = Color(0xFFDE2D42)
    private val BUTTON_ICON_COLOR_LIGHT : Color = Color(0xFFDEDEDE)
    private val BUTTON_ICON_COLOR_DARK : Color = Color(0xFFDEDEDE)

    fun getTheme(): Boolean {
        return dark_theme
    }
    fun setTheme(isDark: Boolean){
        dark_theme = isDark
    }
    fun bgColor(): Color {
        return if(dark_theme) BG_COLOR_DARK else BG_COLOR_LIGHT
    }
    fun textColor(): Color {
        return if(dark_theme) TEXT_COLOR_DARK else TEXT_COLOR_LIGHT
    }
    fun overlayColor(): Color {
        return if(dark_theme) OVERLAY_COLOR_DARK else OVERLAY_COLOR_LIGHT
    }
    fun accentColor(): Color {
        return if(dark_theme) ACCENT_COLOR_DARK else ACCENT_COLOR_LIGHT
    }
    fun accentDeepColor(): Color {
        return if(dark_theme) ACCENT_DEEP_COLOR_DARK else ACCENT_DEEP_COLOR_LIGHT
    }
    fun confirmColor(): Color {
        return if(dark_theme) CONFIRM_COLOR_DARK else CONFIRM_COLOR_LIGHT
    }
    fun rejectColor(): Color {
        return if(dark_theme) REJECT_COLOR_DARK else REJECT_COLOR_LIGHT
    }
    fun buttonIconColor(): Color {
        return if (dark_theme) BUTTON_ICON_COLOR_DARK else BUTTON_ICON_COLOR_LIGHT
    }


    fun flowerBedDoodleDP(): Int {
        return 40
    }
    fun doodlePaddingDP(): Int {
        return 5
    }
    fun flowerBedPaddingDP(): Int {
        return 10
    }


    val playfairFontFamily = FontFamily (
        Font(R.font.playfair_display_regular, weight = FontWeight.Normal),
        Font(R.font.playfair_display_bold, weight = FontWeight.Bold),
        Font(R.font.playfair_display_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(R.font.playfair_display_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
        Font(R.font.playfair_display_black, weight = FontWeight.Black),
        Font(R.font.playfair_display_black_italic, weight = FontWeight.Black, style = FontStyle.Italic),
    )

    val inriaSerifFontFamily = FontFamily (
        Font(R.font.inria_serif_regular, weight = FontWeight.Normal),
        Font(R.font.inria_serif_bold, weight = FontWeight.Bold),
        Font(R.font.inria_serif_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(R.font.inria_serif_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    )

    val hammersmithFontFamily = FontFamily (
        Font(R.font.hammersmith_one_regular, weight = FontWeight.Normal)
    )

}