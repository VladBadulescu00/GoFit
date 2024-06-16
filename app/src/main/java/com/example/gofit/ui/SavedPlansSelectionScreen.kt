package com.example.gofit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlansSelectionScreen(navController: NavController, userId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Plans") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { navController.navigate("saved_fitness_plans/$userId") }) {
                    Text("Saved Fitness Plans")
                }
                Button(onClick = { navController.navigate("saved_meal_plans/$userId") }) {
                    Text("Saved Meal Plans")
                }
            }
        }
    }
}
