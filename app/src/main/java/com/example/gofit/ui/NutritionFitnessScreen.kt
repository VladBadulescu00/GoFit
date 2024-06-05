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
import com.example.gofit.viewmodel.MealPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionFitnessScreen(navController: NavController, mealPlansViewModel: MealPlansViewModel) {
    val viewModel: MealPlansViewModel = mealPlansViewModel

    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var medicalConditions by remember { mutableStateOf("") }

    val genders = listOf("Male", "Female", "Other")
    val activityLevels = listOf("Sedentary", "Lightly active", "Moderately active", "Very active", "Super active")
    val medicalConditionsOptions = listOf("None", "Diabetes", "Hypertension", "Heart Disease")

    var expandedGender by remember { mutableStateOf(false) }
    var expandedActivityLevel by remember { mutableStateOf(false) }
    var expandedMedicalConditions by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Enter Your Details", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Gender Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender }
            ) {
                TextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genders.forEach { selectedGender ->
                        DropdownMenuItem(
                            text = { Text(selectedGender) },
                            onClick = {
                                gender = selectedGender
                                expandedGender = false
                            }
                        )
                    }
                }
            }

            // Activity Level Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedActivityLevel,
                onExpandedChange = { expandedActivityLevel = !expandedActivityLevel }
            ) {
                TextField(
                    value = activityLevel,
                    onValueChange = { activityLevel = it },
                    label = { Text("Activity Level") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedActivityLevel,
                    onDismissRequest = { expandedActivityLevel = false }
                ) {
                    activityLevels.forEach { selectedLevel ->
                        DropdownMenuItem(
                            text = { Text(selectedLevel) },
                            onClick = {
                                activityLevel = selectedLevel
                                expandedActivityLevel = false
                            }
                        )
                    }
                }
            }

            // Medical Conditions Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedMedicalConditions,
                onExpandedChange = { expandedMedicalConditions = !expandedMedicalConditions }
            ) {
                TextField(
                    value = medicalConditions,
                    onValueChange = { medicalConditions = it },
                    label = { Text("Medical Conditions") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMedicalConditions) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedMedicalConditions,
                    onDismissRequest = { expandedMedicalConditions = false }
                ) {
                    medicalConditionsOptions.forEach { condition ->
                        DropdownMenuItem(
                            text = { Text(condition) },
                            onClick = {
                                medicalConditions = condition
                                expandedMedicalConditions = false
                            }
                        )
                    }
                }
            }

            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = {
                    // Validate inputs
                    val ageInt = age.toIntOrNull()
                    val weightFloat = weight.toFloatOrNull()
                    val heightInt = height.toIntOrNull()

                    if (ageInt == null || weightFloat == null || heightInt == null) {
                        showError = true
                        errorMessage = "Please enter valid numbers for age, weight, and height."
                    } else {
                        showError = false
                        // Call ViewModel function to generate meal plans
                        viewModel.generateMealPlans(
                            age = ageInt,
                            weight = weightFloat,
                            height = heightInt,
                            gender = gender,
                            activityLevel = activityLevel,
                            medicalConditions = medicalConditions
                        )
                        navController.navigate("meal_plans")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Generate Meal Plan")
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
