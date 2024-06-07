package com.example.gofit.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gofit.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedMainScreen(navController: NavController, userId: String) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GoFit") },
                actions = {
                    Button(onClick = {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("authenticated_main") { inclusive = true }
                        }
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Log Out")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Generate Plans") },
                    label = { Text("Planuri") },
                    selected = false,
                    onClick = { navController.navigate("nutrition_fitness/$userId") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Browse Meal Plans") },
                    label = { Text("Planurile mele") },
                    selected = false,
                    onClick = { navController.navigate("my_plans") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.ShowChart, contentDescription = "My Progress") },
                    label = { Text("Progresul meu") },
                    selected = false,
                    onClick = { navController.navigate("my_progress") }
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Background Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(id = R.drawable.logogofit),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 50.dp)
                        .size(150.dp)
                )
                Text(
                    text = "Welcome, userId = $userId",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    )
}

