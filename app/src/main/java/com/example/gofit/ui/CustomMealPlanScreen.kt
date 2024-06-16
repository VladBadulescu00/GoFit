package com.example.gofit.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMealPlanScreen(navController: NavController, viewModel: MealPlansViewModel, userId: String) {
    var planName by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Lunch") }
    var selectedMedicalCondition by remember { mutableStateOf("none") }

    var mainDishAmount by remember { mutableStateOf("") }
    var sideDishAmount by remember { mutableStateOf("") }
    var soupAmount by remember { mutableStateOf("") }
    var desertAmount by remember { mutableStateOf("") }
    var breakfastAmount by remember { mutableStateOf("") }

    var selectedMainDish by remember { mutableStateOf<MealComponent?>(null) }
    var selectedSideDish by remember { mutableStateOf<MealComponent?>(null) }
    var selectedSoup by remember { mutableStateOf<MealComponent?>(null) }
    var selectedDesert by remember { mutableStateOf<MealComponent?>(null) }
    var selectedBreakfast by remember { mutableStateOf<MealComponent?>(null) }

    var addedMeals by remember { mutableStateOf(listOf<Pair<MealComponent, Int>>()) }

    val totalCalories = addedMeals.sumOf { it.first.calories * it.second / 100 }
    val totalProtein = addedMeals.sumOf { it.first.protein * it.second / 100 }
    val totalCarbs = addedMeals.sumOf { it.first.carbs * it.second / 100 }
    val totalFats = addedMeals.sumOf { it.first.fats * it.second / 100 }
    val totalSodium = addedMeals.sumOf { it.first.sodium * it.second / 100 }

    val mealTypes = listOf("Breakfast", "Lunch", "Diner")
    val medicalConditions = listOf("None", "Diabetes", "Heart Disease", "Hypertension")

    var expandedMealType by remember { mutableStateOf(false) }
    var expandedMedicalCondition by remember { mutableStateOf(false) }
    var expandedMainDish by remember { mutableStateOf(false) }
    var expandedSideDish by remember { mutableStateOf(false) }
    var expandedSoup by remember { mutableStateOf(false) }
    var expandedDesert by remember { mutableStateOf(false) }
    var expandedBreakfast by remember { mutableStateOf(false) }

    val breakfastComponents by viewModel.breakfastComponents.collectAsState()
    val mainDishComponents by viewModel.mainDishComponents.collectAsState()
    val sideDishComponents by viewModel.sideDishComponents.collectAsState()
    val soupComponents by viewModel.soupComponents.collectAsState()
    val desertComponents by viewModel.desertComponents.collectAsState()

    // Fetch meal components for subcategories
    LaunchedEffect(selectedMealType, selectedMedicalCondition) {
        if (selectedMealType.isNotEmpty() && selectedMedicalCondition.isNotEmpty()) {
            val trimmedCondition = selectedMedicalCondition.trim()
            Log.d("CustomMealPlanScreen", "Selected Medical Condition: $trimmedCondition")

            val formattedMedicalCondition = when (trimmedCondition.lowercase(Locale.getDefault())) {
                "diabetes" -> "diabetes"
                "hypertension" -> "hypertension"
                "none" -> "none"
                else -> "heartDisease"
            }

            Log.d("CustomMealPlanScreen", "Formatted Medical Condition: $formattedMedicalCondition")

            val subCategories = when (selectedMealType) {
                "Breakfast" -> listOf("meals")
                "Lunch" -> listOf("mainDish", "sideDish", "soup", "desert")
                "Diner" -> listOf("mainDish", "sideDish", "desert")
                else -> listOf()
            }

            subCategories.forEach { subCategory ->
                Log.d("CustomMealPlanScreen", "Fetching components for $selectedMealType/$formattedMedicalCondition/$subCategory")
                viewModel.fetchMealComponentsForType(selectedMealType.lowercase(), formattedMedicalCondition, subCategory)
                Log.d("CustomMealPlanScreen", "Querying Firestore for: ${selectedMealType.lowercase()}/$formattedMedicalCondition/$subCategory")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        item {
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
                    value = selectedMedicalCondition.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
                            text = { Text(condition.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) },
                            onClick = {
                                selectedMedicalCondition = condition.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                expandedMedicalCondition = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (selectedMealType == "Breakfast") {
            item {
                // Breakfast Component
                ExposedDropdownMenuBox(
                    expanded = expandedBreakfast,
                    onExpandedChange = { expandedBreakfast = !expandedBreakfast }
                ) {
                    TextField(
                        value = selectedBreakfast?.name ?: "Select Breakfast",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBreakfast) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBreakfast,
                        onDismissRequest = { expandedBreakfast = false }
                    ) {
                        breakfastComponents.forEach { component ->
                            DropdownMenuItem(
                                text = { Text(component.name) },
                                onClick = {
                                    selectedBreakfast = component
                                    expandedBreakfast = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = breakfastAmount,
                    onValueChange = { breakfastAmount = it },
                    label = { Text("Amount (grams)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                // Main Dish Component
                ExposedDropdownMenuBox(
                    expanded = expandedMainDish,
                    onExpandedChange = { expandedMainDish = !expandedMainDish }
                ) {
                    TextField(
                        value = selectedMainDish?.name ?: "Select Main Dish",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMainDish) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMainDish,
                        onDismissRequest = { expandedMainDish = false }
                    ) {
                        mainDishComponents.forEach { component ->
                            DropdownMenuItem(
                                text = { Text(component.name) },
                                onClick = {
                                    selectedMainDish = component
                                    expandedMainDish = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = mainDishAmount,
                    onValueChange = { mainDishAmount = it },
                    label = { Text("Main Dish Amount (grams)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // Side Dish Component
                ExposedDropdownMenuBox(
                    expanded = expandedSideDish,
                    onExpandedChange = { expandedSideDish = !expandedSideDish }
                ) {
                    TextField(
                        value = selectedSideDish?.name ?: "Select Side Dish",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSideDish) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSideDish,
                        onDismissRequest = { expandedSideDish = false }
                    ) {
                        sideDishComponents.forEach { component ->
                            DropdownMenuItem(
                                text = { Text(component.name) },
                                onClick = {
                                    selectedSideDish = component
                                    expandedSideDish = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = sideDishAmount,
                    onValueChange = { sideDishAmount = it },
                    label = { Text("Side Dish Amount (grams)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (selectedMealType == "Lunch") {
                item {
                    // Soup Component
                    ExposedDropdownMenuBox(
                        expanded = expandedSoup,
                        onExpandedChange = { expandedSoup = !expandedSoup }
                    ) {
                        TextField(
                            value = selectedSoup?.name ?: "Select Soup",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSoup) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedSoup,
                            onDismissRequest = { expandedSoup = false }
                        ) {
                            soupComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedSoup = component
                                        expandedSoup = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = soupAmount,
                        onValueChange = { soupAmount = it },
                        label = { Text("Soup Amount (grams)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                // Dessert Component
                ExposedDropdownMenuBox(
                    expanded = expandedDesert,
                    onExpandedChange = { expandedDesert = !expandedDesert }
                ) {
                    TextField(
                        value = selectedDesert?.name ?: "Select Dessert",
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDesert) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDesert,
                        onDismissRequest = { expandedDesert = false }
                    ) {
                        desertComponents.forEach { component ->
                            DropdownMenuItem(
                                text = { Text(component.name) },
                                onClick = {
                                    selectedDesert = component
                                    expandedDesert = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = desertAmount,
                    onValueChange = { desertAmount = it },
                    label = { Text("Dessert Amount (grams)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        val components = listOf(
                            selectedMainDish to mainDishAmount,
                            selectedSideDish to sideDishAmount,
                            selectedSoup to soupAmount,
                            selectedDesert to desertAmount,
                            selectedBreakfast to breakfastAmount
                        )

                        components.forEach { (component, amount) ->
                            val grams = amount.toIntOrNull()
                            if (component != null && grams != null && grams > 0) {
                                addedMeals = addedMeals + Pair(component, grams)
                            }
                        }

                        mainDishAmount = ""
                        sideDishAmount = ""
                        soupAmount = ""
                        desertAmount = ""
                        breakfastAmount = ""
                    },
                ) {
                    Text("Add Meal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Added Meals:", style = MaterialTheme.typography.titleMedium)
            addedMeals.forEach { (meal, amount) ->
                Text("${meal.name}: $amount grams")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Total Calories: $totalCalories", style = MaterialTheme.typography.titleMedium)
            Text("Total Protein: $totalProtein g", style = MaterialTheme.typography.titleMedium)
            Text("Total Carbs: $totalCarbs g", style = MaterialTheme.typography.titleMedium)
            Text("Total Fats: $totalFats g", style = MaterialTheme.typography.titleMedium)
            Text("Total Sodium: $totalSodium mg", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
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
                ) {
                    Text("Save Plan")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
    }
}
