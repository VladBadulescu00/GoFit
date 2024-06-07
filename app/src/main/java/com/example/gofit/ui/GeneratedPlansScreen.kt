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
import com.example.gofit.viewmodel.MealPlansViewModel
import com.example.gofit.viewmodel.FitnessPlansViewModel

@Composable
fun GeneratedPlansScreen(
    navController: NavController,
    mealPlansViewModel: MealPlansViewModel,
    fitnessPlansViewModel: FitnessPlansViewModel,
    userId: String
) {
    val generatedMealPlans by mealPlansViewModel.generatedMealPlans.collectAsState()
    val generatedFitnessPlans by fitnessPlansViewModel.generatedFitnessPlans.collectAsState()
    val dailyCaloricNeeds by mealPlansViewModel.dailyCaloricNeeds.collectAsState()

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
                Text("Planuri de masă", style = MaterialTheme.typography.titleLarge)
                Text("Necesarul caloric zilnic: $dailyCaloricNeeds calorii", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(generatedMealPlans) { mealPlan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Meal: ${mealPlan.meal}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Calories: ${mealPlan.calories}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Protein: ${mealPlan.protein}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Carbs: ${mealPlan.carbs}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Fats: ${mealPlan.fats}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Sodium: ${mealPlan.sodium}mg", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            items(generatedFitnessPlans) { fitnessPlan ->
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("saved_meal_plans/$userId") }) {
                    Text("View Saved Meal Plans")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate("saved_fitness_plans/$userId") }) {
                    Text("View Saved Fitness Plans")
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
