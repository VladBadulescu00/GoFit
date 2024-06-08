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
import com.example.gofit.viewmodel.FitnessPlan2
import com.example.gofit.viewmodel.FitnessPlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFitnessPlanScreen(navController: NavController, viewModel: FitnessPlansViewModel, userId: String) {
    var planName by remember { mutableStateOf("") }
    var selectedExerciseType by remember { mutableStateOf("Chest") }
    var selectedExercise by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val exerciseTypes = listOf("Chest", "Back", "Biceps", "Triceps", "Shoulders", "Legs", "Cardio")
    val exercises = InitialData.generateFitnessPlansForUser(userId).map { it.exercise }

    var addedExercises by remember { mutableStateOf(listOf<Triple<String, Int, Int>>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Custom Fitness Plan") },
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

                // Exercise Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    TextField(
                        value = selectedExerciseType,
                        onValueChange = {},
                        label = { Text("Exercise Type") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        exerciseTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { selectedExerciseType = type }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Exercise Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {
                    TextField(
                        value = selectedExercise,
                        onValueChange = {},
                        label = { Text("Exercise") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        exercises.forEach { exercise ->
                            DropdownMenuItem(
                                text = { Text(exercise) },
                                onClick = { selectedExercise = exercise }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedExerciseType == "Cardio") {
                    TextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    TextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text("Sets") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val setCount = sets.toIntOrNull() ?: 0
                        val repCount = reps.toIntOrNull() ?: 0
                        val durationCount = duration.toIntOrNull() ?: 0
                        if (selectedExerciseType == "Cardio") {
                            addedExercises = addedExercises + Triple(selectedExercise, 0, durationCount)
                        } else {
                            addedExercises = addedExercises + Triple(selectedExercise, setCount, repCount)
                        }
                        sets = ""
                        reps = ""
                        duration = ""
                        selectedExercise = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Exercise")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Added Exercises:", style = MaterialTheme.typography.titleMedium)
                addedExercises.forEach { (exercise, setCount, repOrDuration) ->
                    Text("$exercise: ${if (setCount > 0) "$setCount sets, $repOrDuration reps" else "$repOrDuration minutes"}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Save the custom fitness plan
                        val fitnessPlan = FitnessPlan2(
                            id = 0,
                            name = planName,
                            exercises = addedExercises.map { "${it.first}: ${if (it.second > 0) "${it.second} sets, ${it.third} reps" else "${it.third} minutes"}" }
                        )
                        viewModel.saveFitnessPlan(fitnessPlan, userId)
                        navController.navigate("saved_fitness_plans/$userId")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Plan")
                }
            }
        }
    )
}

