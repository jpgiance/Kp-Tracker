package com.autonomy_lab.kptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.autonomy_lab.kptracker.ui.main.MainScreen
import com.autonomy_lab.kptracker.ui.theme.KpTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            KpTrackerTheme {
                MainScreen()
            }
        }
    }
}

