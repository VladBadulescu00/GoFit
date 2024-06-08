package com.example.gofit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gofit.data.InitialData
import com.example.gofit.viewmodel.MealPlan2
import com.example.gofit.viewmodel.MealPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMealPlanScreen(navController: NavController, viewModel: MealPlansViewModel, userId: String) {
    var planName by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var selectedMeal by remember { mutableStateOf("") }
    var mealAmount by remember { mutableStateOf("") }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")
    val meals = InitialData.generateMealPlansForUser(userId).map { it.meal }

    var addedMeals by remember { mutableStateOf(listOf<Pair<String, Int>>()) }
    val totalCalories = addedMeals.sumOf { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Meal Plan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                TextField(
                    value = planName,
                    onValueChange = { planName = it },
                    label = { Text("Plan Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Meal Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    TextField(
                        value = selectedMealType,
                        onValueChange = {},
                        label = { Text("Meal Type") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { selectedMealType = type }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Meal Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    TextField(
                        value = selectedMeal,
                        onValueChange = {},
                        label = { Text("Meal") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        meals.forEach { meal ->
                            DropdownMenuItem(
                                text = { Text(meal) },
                                onClick = { selectedMeal = meal }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = mealAmount,
                    onValueChange = { mealAmount = it },
                    label = { Text("Amount (grams)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val amount = mealAmount.toIntOrNull() ?: 0
                        addedMeals = addedMeals + Pair(selectedMeal, amount)
                        mealAmount = ""
                        selectedMeal = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Meal")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Added Meals:", style = MaterialTheme.typography.titleMedium)
                addedMeals.forEach { (meal, amount) ->
                    Text("$meal: $amount grams")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Total Calories: $totalCalories", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Save the custom meal plan
                        val mealPlan = MealPlan2(
                            id = 0,
                            name = planName,
                            meals = addedMeals.map { it.first }
                        )
                        viewModel.saveMealPlan(mealPlan, userId)
                        navController.navigate("saved_meal_plans/$userId")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Plan")
                }
            }
        }
    )
}

