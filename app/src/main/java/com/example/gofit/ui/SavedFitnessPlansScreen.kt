// SavedFitnessPlansScreen.kt
package com.example.gofit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gofit.viewmodel.FitnessPlansViewModel

@Composable
fun SavedFitnessPlansScreen(navController: NavController, viewModel: FitnessPlansViewModel) {
    val fitnessPlans by viewModel.fitnessPlans.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSavedFitnessPlans()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Text("Saved Fitness Plans", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(fitnessPlans) { fitnessPlan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Exercise: ${fitnessPlan.exercise}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Duration: ${fitnessPlan.duration} minutes", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Calories Burned: ${fitnessPlan.caloriesBurned}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}
