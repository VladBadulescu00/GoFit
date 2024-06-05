// SavedMealPlansScreen.kt
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

@Composable
fun SavedMealPlansScreen(navController: NavController, viewModel: MealPlansViewModel) {
    val mealPlans by viewModel.mealPlans.collectAsState()

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
                Text("Saved Meal Plans", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(mealPlans) { mealPlan ->
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
