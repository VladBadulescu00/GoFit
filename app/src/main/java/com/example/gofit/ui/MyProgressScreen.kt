package com.example.gofit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gofit.R

@Composable
fun MyProgressScreen(navController: NavController) {
    // Example progress data
    val progressData = listOf("Weight: 70kg", "Calories: 2000kcal", "Protein: 100g")

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(text = "Progresul Meu", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            progressData.forEach { progress ->
                Text(
                    text = progress,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}
