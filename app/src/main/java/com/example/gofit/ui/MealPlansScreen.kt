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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gofit.viewmodel.MealPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlansScreen(navController: NavController, viewModel: MealPlansViewModel) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val dailyCaloricNeeds by viewModel.dailyCaloricNeeds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSavedMealPlans()
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
                Text("Planuri de masă", style = MaterialTheme.typography.titleLarge)
                Text("Necesarul caloric zilnic: $dailyCaloricNeeds calorii", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(mealPlans) { mealPlan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Masa: ${mealPlan.meal}", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Calorii: ${mealPlan.calories}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Proteine: ${mealPlan.protein}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Carbohidrați: ${mealPlan.carbs}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Grăsimi: ${mealPlan.fats}g", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Sodiu: ${mealPlan.sodium}mg", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                Button(onClick = {
                    navController.navigate("saved_meal_plans")
                }) {
                    Text("View Saved Meal Plans")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate("saved_fitness_plans")
                }) {
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
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}
