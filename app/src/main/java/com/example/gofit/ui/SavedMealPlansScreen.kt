package com.example.gofit.ui

import android.util.Log
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.gofit.viewmodel.CustomMealPlan
import com.example.gofit.viewmodel.MealPlan
import com.example.gofit.viewmodel.MealPlansViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedMealPlansScreen(
    navController: NavController,
    viewModel: MealPlansViewModel,
    userId: String
) {
    val savedMealPlansWithFactors by viewModel.savedMealPlansWithFactors.collectAsState()
    val customMealPlans by viewModel.customMealPlans.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSavedMealPlansWithFactors(userId)
        viewModel.getCustomSavedMealPlans(userId)
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

            LazyColumn {
                items(savedMealPlansWithFactors) { (plan, factor) ->
                    MealPlanCard(plan = plan, factor = factor, onDelete = {
                        viewModel.deleteMealPlan(plan.id, userId)
                    })
                }

                items(customMealPlans) { customPlan ->
                    CustomMealPlanCard(customPlan = customPlan, viewModel = viewModel, onDelete = {
                        viewModel.deleteCustomMealPlan(customPlan.id, userId)
                    })
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
@Composable
fun MealPlanCard(plan: MealPlan, factor: Double, onDelete: () -> Unit) {
    val gramsPerMeal = if (factor > 0) (plan.grams * factor).toInt() else 0

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
            Text("Fiber: ${plan.fiber}g", style = MaterialTheme.typography.bodyMedium)
            Text("Grams: ${plan.grams} g", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}


@Composable
fun CustomMealPlanCard(customPlan: CustomMealPlan, viewModel: MealPlansViewModel, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Custom Plan: ${customPlan.name}", style = MaterialTheme.typography.titleMedium)
            customPlan.meals.forEach { meal ->
                val targetCalories = when (meal.name.lowercase()) {
                    "breakfast" -> viewModel.dailyCaloricNeeds.value * 0.25
                    "lunch" -> viewModel.dailyCaloricNeeds.value * 0.40
                    "diner" -> viewModel.dailyCaloricNeeds.value * 0.35
                    else -> 0.0
                }

                val factor = if (meal.calories > 0) targetCalories / meal.calories else 0.0
                val gramsPerMeal = if (factor > 0) (100 * factor).toInt() else 0

                Text("Meal: ${meal.name}", style = MaterialTheme.typography.bodyMedium)
                Text("Calories: ${meal.calories}", style = MaterialTheme.typography.bodySmall)
                Text("Protein: ${meal.protein}g", style = MaterialTheme.typography.bodySmall)
                Text("Carbs: ${meal.carbs}g", style = MaterialTheme.typography.bodySmall)
                Text("Fats: ${meal.fats}g", style = MaterialTheme.typography.bodySmall)
                Text("Sodium: ${meal.sodium}mg", style = MaterialTheme.typography.bodySmall)
                Text("Grams per Meal: ${gramsPerMeal} g", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = onDelete,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Delete")
            }
        }
    }
}
