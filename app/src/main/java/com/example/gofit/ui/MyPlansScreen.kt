package com.example.gofit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gofit.viewmodel.MyPlansViewModel
import com.example.gofit.viewmodel.MealPlan2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlansScreen(navController: NavController) {
    val viewModel: MyPlansViewModel = viewModel()
    val myPlans = viewModel.myPlans
    var showDialog by remember { mutableStateOf(false) }
    var planToEdit by remember { mutableStateOf<MealPlan2?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Text("Planurile mele", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(myPlans) { plan ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            planToEdit = plan
                            showDialog = true
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(plan.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            plan.meals.forEach { meal ->
                                Text(meal, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    planToEdit = null
                    showDialog = true
                },
                modifier = Modifier.align(Alignment.End).padding(16.dp)
            ) {
                Text("Add Plan")
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

    if (showDialog) {
        MealPlanDialog(
            initialPlan = planToEdit,
            onSave = { plan ->
                if (plan.id == 0) {
                    viewModel.addMealPlan(plan.copy(id = (myPlans.maxOfOrNull { it.id } ?: 0) + 1))
                } else {
                    viewModel.editMealPlan(plan)
                }
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun MealPlanDialog(initialPlan: MealPlan2?, onSave: (MealPlan2) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(initialPlan?.name ?: "") }
    var meals by remember { mutableStateOf(initialPlan?.meals?.joinToString("\n") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialPlan == null) "Add Meal Plan" else "Edit Meal Plan") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plan Name") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                TextField(
                    value = meals,
                    onValueChange = { meals = it },
                    label = { Text("Meals (one per line)") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val planMeals = meals.split("\n").map { it.trim() }
                    onSave(MealPlan2(initialPlan?.id ?: 0, name, planMeals))
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
