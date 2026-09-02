package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainScreen
import com.example.ui.theme.RhythmPlayTheme
import com.example.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {
    private val musicViewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RhythmPlayTheme {
                MainScreen(viewModel = musicViewModel)
            }
        }
    }
}

