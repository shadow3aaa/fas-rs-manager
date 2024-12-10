package com.shadow3.fas_rsmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shadow3.fas_rsmanager.ui.screens.App
import com.shadow3.fas_rsmanager.ui.theme.FasrsManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FasrsManagerTheme {
                App()
            }
        }
    }
}
