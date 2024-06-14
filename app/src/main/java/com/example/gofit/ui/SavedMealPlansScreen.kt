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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMealPlansScreen(
    navController: NavController,
    viewModel: MealPlansViewModel,
    userId: String
) {
    val savedMealPlans by viewModel.savedMealPlans.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSavedMealPlans(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Text("Saved Meal Plans", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(savedMealPlans) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${plan.type}: ${plan.meal}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Calories: ${plan.calories}", style = MaterialTheme.typography.bodyMedium)
                            Text("Protein: ${plan.protein}g", style = MaterialTheme.typography.bodyMedium)
                            Text("Carbs: ${plan.carbs}g", style = MaterialTheme.typography.bodyMedium)
                            Text("Fats: ${plan.fats}g", style = MaterialTheme.typography.bodyMedium)
                            Text("Sodium: ${plan.sodium}mg", style = MaterialTheme.typography.bodyMedium)
                            Button(
                                onClick = { viewModel.deleteMealPlan(plan.id, userId) },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Delete")
                            }
                        }
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
