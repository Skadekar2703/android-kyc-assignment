package com.tommy.digitalbankkyc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.tommy.digitalbankkyc.presentation.navigation.AppNavGraph
import com.tommy.digitalbankkyc.presentation.ui.theme.DigitalBankKYCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigitalBankKycRoot()
        }
    }
}

@Composable
private fun DigitalBankKycRoot() {
    val systemDark = isSystemInDarkTheme()
    var isDarkTheme by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val darkTheme = isDarkTheme ?: systemDark

    DigitalBankKYCTheme(darkTheme = darkTheme) {
        AppNavGraph(
            darkTheme = darkTheme,
            onToggleDarkTheme = { isDarkTheme = !darkTheme }
        )
    }
}
