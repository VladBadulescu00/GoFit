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
import com.example.gofit.viewmodel.FitnessPlan
import com.example.gofit.viewmodel.CustomFitnessPlan

@Composable
fun SavedFitnessPlansScreen(navController: NavController, viewModel: FitnessPlansViewModel, userId: String) {
    val fitnessPlans by viewModel.savedFitnessPlans.collectAsState()
    val customFitnessPlans by viewModel.customFitnessPlans.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSavedFitnessPlans(userId)
        viewModel.getCustomSavedFitnessPlans(userId) // Fetch custom fitness plans
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
                FitnessPlanCard(fitnessPlan, onDelete = { viewModel.deleteFitnessPlan(fitnessPlan.id, userId) })
            }

            items(customFitnessPlans) { customPlan ->
                CustomFitnessPlanCard(customPlan, onDelete = { viewModel.deleteCustomFitnessPlan(customPlan.id, userId) })
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

@Composable
fun FitnessPlanCard(fitnessPlan: FitnessPlan, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Exercise: ${fitnessPlan.exercise}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Duration: ${fitnessPlan.duration} minutes", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Calories Burned: ${fitnessPlan.caloriesBurned}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun CustomFitnessPlanCard(customPlan: CustomFitnessPlan, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Custom Plan: ${customPlan.name}", style = MaterialTheme.typography.titleMedium)
            customPlan.exercises.forEach { exercise ->
                Text("${exercise.name}: ${exercise.reps} reps, ${exercise.sets} sets", style = MaterialTheme.typography.bodyMedium)
                Text("Calories Burned: ${exercise.caloriesBurned}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(onClick = onDelete, modifier = Modifier.padding(top = 8.dp)) {
                Text("Delete")
            }
        }
    }
}
