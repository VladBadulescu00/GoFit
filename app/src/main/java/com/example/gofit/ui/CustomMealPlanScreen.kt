package com.example.gofit.ui

import android.util.Log
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
import androidx.navigation.NavController
import com.example.gofit.viewmodel.MealComponent
import com.example.gofit.viewmodel.CustomMealPlan
import com.example.gofit.viewmodel.MealPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMealPlanScreen(navController: NavController, viewModel: MealPlansViewModel, userId: String) {
    var planName by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var selectedMedicalCondition by remember { mutableStateOf("none") }
    var subCategory by remember { mutableStateOf("") }
    var mealAmount by remember { mutableStateOf("") }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner")
    val medicalConditions = listOf("none", "diabetes", "heartDisease", "hypertension")
    val subCategories = listOf("mainDish", "sideDish", "dessert", "soup")

    var selectedComponent by remember { mutableStateOf<MealComponent?>(null) }
    var addedMeals by remember { mutableStateOf(listOf<Pair<MealComponent, Int>>()) }
    val totalCalories = addedMeals.sumOf { it.first.calories * it.second / 100 }

    var expandedMealType by remember { mutableStateOf(false) }
    var expandedMedicalCondition by remember { mutableStateOf(false) }
    var expandedSubCategory by remember { mutableStateOf(false) }
    var expandedComponent by remember { mutableStateOf(false) }

    // LaunchedEffect to fetch meal components based on selected options
    LaunchedEffect(selectedMealType, selectedMedicalCondition, subCategory) {
        if (selectedMealType.isNotEmpty() && selectedMedicalCondition.isNotEmpty() && subCategory.isNotEmpty()) {
            viewModel.fetchMealComponentsForType(selectedMealType, selectedMedicalCondition, subCategory)
            Log.d("CustomMealPlanScreen", "Querying Firestore for: $selectedMealType/$selectedMedicalCondition/$subCategory")
        }
    }

    val mealComponents by viewModel.mealComponents.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Text("Create Custom Meal Plan", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Meal Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMealType,
                onExpandedChange = { expandedMealType = !expandedMealType }
            ) {
                TextField(
                    value = selectedMealType,
                    onValueChange = { },
                    label = { Text("Meal Type") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMealType) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedMealType,
                    onDismissRequest = { expandedMealType = false }
                ) {
                    mealTypes.forEach { mealType ->
                        DropdownMenuItem(
                            text = { Text(mealType) },
                            onClick = {
                                selectedMealType = mealType
                                expandedMealType = false
                                subCategory = "" // Reset subcategory when meal type changes
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Medical Condition Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMedicalCondition,
                onExpandedChange = { expandedMedicalCondition = !expandedMedicalCondition }
            ) {
                TextField(
                    value = selectedMedicalCondition,
                    onValueChange = { },
                    label = { Text("Medical Condition") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMedicalCondition) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedMedicalCondition,
                    onDismissRequest = { expandedMedicalCondition = false }
                ) {
                    medicalConditions.forEach { condition ->
                        DropdownMenuItem(
                            text = { Text(condition) },
                            onClick = {
                                selectedMedicalCondition = condition
                                expandedMedicalCondition = false
                                subCategory = "" // Reset subcategory when medical condition changes
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Subcategory Dropdown
            if (selectedMealType != "Breakfast") {
                ExposedDropdownMenuBox(
                    expanded = expandedSubCategory,
                    onExpandedChange = { expandedSubCategory = !expandedSubCategory }
                ) {
                    TextField(
                        value = subCategory,
                        onValueChange = { },
                        label = { Text("Select SubCategory") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubCategory) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSubCategory,
                        onDismissRequest = { expandedSubCategory = false }
                    ) {
                        val categories = if (selectedMealType == "Lunch") {
                            listOf("mainDish", "sideDish", "soup", "dessert")
                        } else {
                            listOf("mainDish", "sideDish", "dessert")
                        }
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    subCategory = category
                                    expandedSubCategory = false
                                }
                            )
                        }
                    }
                }
            } else {
                subCategory = "mainDish" // Default for Breakfast
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Meal Component Dropdown
            if (subCategory.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = expandedComponent,
                    onExpandedChange = { expandedComponent = !expandedComponent }
                ) {
                    TextField(
                        value = selectedComponent?.name ?: "",
                        onValueChange = { },
                        label = { Text("Select Component") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedComponent) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedComponent,
                        onDismissRequest = { expandedComponent = false }
                    ) {
                        mealComponents.forEach { component ->
                            DropdownMenuItem(
                                text = { Text(component.name) },
                                onClick = {
                                    selectedComponent = component
                                    expandedComponent = false
                                }
                            )
                        }
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
                    val amount = mealAmount.toIntOrNull()
                    if (selectedComponent != null && amount != null && amount > 0) {
                        addedMeals = addedMeals + Pair(selectedComponent!!, amount)
                        mealAmount = ""
                        selectedComponent = null
                    } else {
                        Log.d("AddMeal", "Invalid input for adding meal")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Meal")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Added Meals:", style = MaterialTheme.typography.titleMedium)
            addedMeals.forEach { (meal, amount) ->
                Text("${meal.name}: $amount grams")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Total Calories: $totalCalories", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (planName.isNotEmpty() && addedMeals.isNotEmpty()) {
                        val mealPlan = CustomMealPlan(
                            id = "",
                            userId = userId,
                            name = planName,
                            meals = addedMeals.map { it.first }
                        )
                        viewModel.saveCustomMealPlan(mealPlan)
                        navController.navigate("saved_meal_plans/$userId")
                    } else {
                        Log.d("Save Plan", "Invalid input for saving plan")
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Plan")
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
