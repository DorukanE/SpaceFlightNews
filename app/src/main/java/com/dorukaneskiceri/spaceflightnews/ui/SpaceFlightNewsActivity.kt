package com.dorukaneskiceri.spaceflightnews.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.dorukaneskiceri.spaceflightnews.ui.theme.SpaceFlightNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpaceFlightNewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            SpaceFlightNewsTheme {
                SpaceFlightNewsApp(navController)
            }
        }
    }
}