package com.akeemrotimi.chatapp.core.ui

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun StatusBarTheme(
    color: Color = Color.White,
    lightIcons: Boolean = false,
) {
    val context = LocalContext.current
    val activity =
        remember(context) {
            context as? Activity ?: (context as? ContextWrapper)?.baseContext as? Activity
        }

    LaunchedEffect(color, lightIcons) {
        activity?.window?.let { window ->
            try {
                window.statusBarColor = color.toArgb()
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = lightIcons
                }
            } catch (e: Exception) {
                window.statusBarColor = color.toArgb()
            }
        }
    }
}
