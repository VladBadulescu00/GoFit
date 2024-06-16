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
import com.example.gofit.viewmodel.FitnessComponent
import com.example.gofit.viewmodel.CustomFitnessPlan
import com.example.gofit.viewmodel.FitnessPlansViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFitnessPlanScreen(navController: NavController, viewModel: FitnessPlansViewModel, userId: String) {
    var planName by remember { mutableStateOf("") }
    var selectedFitnessType by remember { mutableStateOf("") }
    var selectedMedicalCondition by remember { mutableStateOf("") }

    var chestReps by remember { mutableStateOf("") }
    var chestSets by remember { mutableStateOf("") }
    var shouldersReps by remember { mutableStateOf("") }
    var shouldersSets by remember { mutableStateOf("") }
    var tricepsReps by remember { mutableStateOf("") }
    var tricepsSets by remember { mutableStateOf("") }
    var backReps by remember { mutableStateOf("") }
    var backSets by remember { mutableStateOf("") }
    var bicepsReps by remember { mutableStateOf("") }
    var bicepsSets by remember { mutableStateOf("") }
    var absReps by remember { mutableStateOf("") }
    var absSets by remember { mutableStateOf("") }
    var legsReps by remember { mutableStateOf("") }
    var legsSets by remember { mutableStateOf("") }
    var exerciseDuration by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var selectedChest by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedShoulders by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedTriceps by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedBack by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedBiceps by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedAbs by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedLegs by remember { mutableStateOf<FitnessComponent?>(null) }
    var selectedExercise by remember { mutableStateOf<FitnessComponent?>(null) }

    var addedExercises by remember { mutableStateOf(listOf<CustomFitnessPlan.FitnessExercise>()) }

    val fitnessTypes = listOf("Push", "Pull", "Cardio", "Lower Body")
    val medicalConditions = listOf("None", "Diabetes", "Heart Disease", "Hypertension")

    var expandedFitnessType by remember { mutableStateOf(false) }
    var expandedMedicalCondition by remember { mutableStateOf(false) }
    var expandedChest by remember { mutableStateOf(false) }
    var expandedShoulders by remember { mutableStateOf(false) }
    var expandedTriceps by remember { mutableStateOf(false) }
    var expandedBack by remember { mutableStateOf(false) }
    var expandedBiceps by remember { mutableStateOf(false) }
    var expandedAbs by remember { mutableStateOf(false) }
    var expandedLegs by remember { mutableStateOf(false) }
    var expandedExercise by remember { mutableStateOf(false) }

    val chestComponents by viewModel.chestComponents.collectAsState()
    val shouldersComponents by viewModel.shouldersComponents.collectAsState()
    val tricepsComponents by viewModel.tricepsComponents.collectAsState()
    val backComponents by viewModel.backComponents.collectAsState()
    val bicepsComponents by viewModel.bicepsComponents.collectAsState()
    val absComponents by viewModel.absComponents.collectAsState()
    val legsComponents by viewModel.legsComponents.collectAsState()
    val exerciseComponents by viewModel.exerciseComponents.collectAsState()

    // Fetch fitness components for subcategories
    LaunchedEffect(selectedFitnessType, selectedMedicalCondition) {
        if (selectedFitnessType.isNotEmpty() && selectedMedicalCondition.isNotEmpty()) {
            val formattedMedicalCondition = when (selectedMedicalCondition.lowercase(Locale.getDefault()).trim()) {
                "diabetes" -> "diabetes"
                "hypertension" -> "hypertension"
                "none" -> "none"
                else -> "heartDisease"
            }

            val formattedFitnessType = when (selectedFitnessType) {
                "Lower Body" -> "lowerBody"
                else -> selectedFitnessType.lowercase(Locale.getDefault())
            }

            val subCategories = when (selectedFitnessType) {
                "Push" -> listOf("chest", "shoulders", "triceps")
                "Pull" -> listOf("back", "biceps")
                "Lower Body" -> listOf("abs", "legs")
                "Cardio" -> listOf("exercise")
                else -> listOf()
            }

            subCategories.forEach { subCategory ->
                Log.d("CustomFitnessPlanScreen", "Fetching components for $formattedFitnessType/$formattedMedicalCondition/$subCategory")
                viewModel.fetchFitnessComponentsForType(formattedFitnessType, formattedMedicalCondition, subCategory)
                Log.d("CustomFitnessPlanScreen", "Querying Firestore for: $formattedFitnessType/$formattedMedicalCondition/$subCategory")
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
            Text("Create Custom Fitness Plan", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Fitness Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedFitnessType,
                onExpandedChange = { expandedFitnessType = !expandedFitnessType }
            ) {
                TextField(
                    value = if (selectedFitnessType.isEmpty()) "Select Fitness Type" else selectedFitnessType,
                    onValueChange = { },
                    label = { Text("Fitness Type") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFitnessType) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedFitnessType,
                    onDismissRequest = { expandedFitnessType = false }
                ) {
                    fitnessTypes.forEach { fitnessType ->
                        DropdownMenuItem(
                            text = { Text(fitnessType) },
                            onClick = {
                                selectedFitnessType = fitnessType
                                expandedFitnessType = false
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
                    value = if (selectedMedicalCondition.isEmpty()) "Select Medical Condition" else selectedMedicalCondition,
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
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        when (selectedFitnessType) {
            "Push" -> {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedChest,
                        onExpandedChange = { expandedChest = !expandedChest }
                    ) {
                        TextField(
                            value = selectedChest?.name ?: "Select Chest Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedChest) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedChest,
                            onDismissRequest = { expandedChest = false }
                        ) {
                            chestComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedChest = component
                                        expandedChest = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = chestReps,
                        onValueChange = { chestReps = it },
                        label = { Text("Chest Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = chestSets,
                        onValueChange = { chestSets = it },
                        label = { Text("Chest Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedShoulders,
                        onExpandedChange = { expandedShoulders = !expandedShoulders }
                    ) {
                        TextField(
                            value = selectedShoulders?.name ?: "Select Shoulders Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedShoulders) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedShoulders,
                            onDismissRequest = { expandedShoulders = false }
                        ) {
                            shouldersComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedShoulders = component
                                        expandedShoulders = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = shouldersReps,
                        onValueChange = { shouldersReps = it },
                        label = { Text("Shoulders Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = shouldersSets,
                        onValueChange = { shouldersSets = it },
                        label = { Text("Shoulders Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedTriceps,
                        onExpandedChange = { expandedTriceps = !expandedTriceps }
                    ) {
                        TextField(
                            value = selectedTriceps?.name ?: "Select Triceps Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTriceps) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTriceps,
                            onDismissRequest = { expandedTriceps = false }
                        ) {
                            tricepsComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedTriceps = component
                                        expandedTriceps = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = tricepsReps,
                        onValueChange = { tricepsReps = it },
                        label = { Text("Triceps Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = tricepsSets,
                        onValueChange = { tricepsSets = it },
                        label = { Text("Triceps Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            "Pull" -> {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedBack,
                        onExpandedChange = { expandedBack = !expandedBack }
                    ) {
                        TextField(
                            value = selectedBack?.name ?: "Select Back Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBack) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBack,
                            onDismissRequest = { expandedBack = false }
                        ) {
                            backComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedBack = component
                                        expandedBack = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = backReps,
                        onValueChange = { backReps = it },
                        label = { Text("Back Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = backSets,
                        onValueChange = { backSets = it },
                        label = { Text("Back Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedBiceps,
                        onExpandedChange = { expandedBiceps = !expandedBiceps }
                    ) {
                        TextField(
                            value = selectedBiceps?.name ?: "Select Biceps Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBiceps) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBiceps,
                            onDismissRequest = { expandedBiceps = false }
                        ) {
                            bicepsComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedBiceps = component
                                        expandedBiceps = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = bicepsReps,
                        onValueChange = { bicepsReps = it },
                        label = { Text("Biceps Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = bicepsSets,
                        onValueChange = { bicepsSets = it },
                        label = { Text("Biceps Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            "Lower Body" -> {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedAbs,
                        onExpandedChange = { expandedAbs = !expandedAbs }
                    ) {
                        TextField(
                            value = selectedAbs?.name ?: "Select Abs Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAbs) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedAbs,
                            onDismissRequest = { expandedAbs = false }
                        ) {
                            absComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedAbs = component
                                        expandedAbs = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = absReps,
                        onValueChange = { absReps = it },
                        label = { Text("Abs Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = absSets,
                        onValueChange = { absSets = it },
                        label = { Text("Abs Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedLegs,
                        onExpandedChange = { expandedLegs = !expandedLegs }
                    ) {
                        TextField(
                            value = selectedLegs?.name ?: "Select Legs Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLegs) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedLegs,
                            onDismissRequest = { expandedLegs = false }
                        ) {
                            legsComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedLegs = component
                                        expandedLegs = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = legsReps,
                        onValueChange = { legsReps = it },
                        label = { Text("Legs Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = legsSets,
                        onValueChange = { legsSets = it },
                        label = { Text("Legs Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            "Cardio" -> {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedExercise,
                        onExpandedChange = { expandedExercise = !expandedExercise }
                    ) {
                        TextField(
                            value = selectedExercise?.name ?: "Select Cardio Exercise",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExercise) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedExercise,
                            onDismissRequest = { expandedExercise = false }
                        ) {
                            exerciseComponents.forEach { component ->
                                DropdownMenuItem(
                                    text = { Text(component.name) },
                                    onClick = {
                                        selectedExercise = component
                                        expandedExercise = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = exerciseDuration,
                        onValueChange = { exerciseDuration = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val caloriesBurned = selectedExercise?.let {
                        (it.calories * (exerciseDuration.toIntOrNull() ?: 0) / 60 * (weight.toIntOrNull() ?: 0) / 75).toString()
                    } ?: "0"

                    Text("Calories Burned: $caloriesBurned", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
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
                        val exercise = when (selectedFitnessType) {
                            "Push" -> listOfNotNull(
                                selectedChest?.let { CustomFitnessPlan.FitnessExercise(it.name, chestReps.toIntOrNull() ?: 0, chestSets.toIntOrNull() ?: 0) },
                                selectedShoulders?.let { CustomFitnessPlan.FitnessExercise(it.name, shouldersReps.toIntOrNull() ?: 0, shouldersSets.toIntOrNull() ?: 0) },
                                selectedTriceps?.let { CustomFitnessPlan.FitnessExercise(it.name, tricepsReps.toIntOrNull() ?: 0, tricepsSets.toIntOrNull() ?: 0) }
                            )
                            "Pull" -> listOfNotNull(
                                selectedBack?.let { CustomFitnessPlan.FitnessExercise(it.name, backReps.toIntOrNull() ?: 0, backSets.toIntOrNull() ?: 0) },
                                selectedBiceps?.let { CustomFitnessPlan.FitnessExercise(it.name, bicepsReps.toIntOrNull() ?: 0, bicepsSets.toIntOrNull() ?: 0) }
                            )
                            "Lower Body" -> listOfNotNull(
                                selectedAbs?.let { CustomFitnessPlan.FitnessExercise(it.name, absReps.toIntOrNull() ?: 0, absSets.toIntOrNull() ?: 0) },
                                selectedLegs?.let { CustomFitnessPlan.FitnessExercise(it.name, legsReps.toIntOrNull() ?: 0, legsSets.toIntOrNull() ?: 0) }
                            )
                            "Cardio" -> listOfNotNull(
                                selectedExercise?.let {
                                    CustomFitnessPlan.FitnessExercise(it.name, duration = exerciseDuration.toIntOrNull() ?: 0, caloriesBurned = (it.calories * (exerciseDuration.toIntOrNull() ?: 0) / 60 * (weight.toIntOrNull() ?: 0) / 75).toInt())
                                }
                            )
                            else -> emptyList()
                        }

                        if (exercise.isNotEmpty()) {
                            addedExercises = addedExercises + exercise
                            // Clear only the exercise-specific fields
                            chestReps = ""
                            chestSets = ""
                            shouldersReps = ""
                            shouldersSets = ""
                            tricepsReps = ""
                            tricepsSets = ""
                            backReps = ""
                            backSets = ""
                            bicepsReps = ""
                            bicepsSets = ""
                            absReps = ""
                            absSets = ""
                            legsReps = ""
                            legsSets = ""
                            exerciseDuration = ""
                            weight = ""
                            selectedChest = null
                            selectedShoulders = null
                            selectedTriceps = null
                            selectedBack = null
                            selectedBiceps = null
                            selectedAbs = null
                            selectedLegs = null
                            selectedExercise = null
                        } else {
                            Log.d("AddExercise", "Invalid input for adding exercise")
                        }
                    },
                ) {
                    Text("Add Exercise")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Added Exercises:", style = MaterialTheme.typography.titleMedium)
            addedExercises.forEach { exercise ->
                if (selectedFitnessType == "Cardio") {
                    Text("${exercise.name}: ${exercise.duration} minutes, ${exercise.caloriesBurned} calories burned")
                } else {
                    Text("${exercise.name}: ${exercise.reps} reps, ${exercise.sets} sets")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = {
                        if (planName.isNotEmpty() && addedExercises.isNotEmpty()) {
                            val fitnessPlan = CustomFitnessPlan(
                                id = "",
                                userId = userId,
                                name = planName,
                                exercises = addedExercises
                            )
                            viewModel.saveCustomFitnessPlan(fitnessPlan)
                            navController.navigate("saved_fitness_plans/$userId")
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
