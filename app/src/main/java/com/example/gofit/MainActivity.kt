package com.example.gofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.gofit.navigation.Navigation
import com.example.gofit.ui.theme.GoFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoFitTheme {
                val navController = rememberNavController()
                Navigation(navController)
            }
        }
    }
}

