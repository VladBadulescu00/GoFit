package com.example.gofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.gofit.navigation.Navigation
import com.example.gofit.ui.theme.GoFitTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            GoFitTheme {
                val navController = rememberNavController()
                Navigation(navController)
            }
        }
    }
}
