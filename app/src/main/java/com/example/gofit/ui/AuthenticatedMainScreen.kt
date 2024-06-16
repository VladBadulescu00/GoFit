package com.example.gofit.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gofit.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedMainScreen(navController: NavController, userId: String) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email
    val username = userEmail?.substringBefore('@') ?: "User"

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
                    label = { Text("Generate Plan") },
                    selected = false,
                    onClick = { navController.navigate("nutrition_fitness/$userId") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Create Custom Plan") },
                    label = { Text("Create Custom Plan") },
                    selected = false,
                    onClick = { navController.navigate("custom_plans/$userId") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Save, contentDescription = "Saved Plans") },
                    label = { Text("Saved Plans") },
                    selected = false,
                    onClick = { navController.navigate("saved_plans_selection/$userId") }
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logogofit),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(top = 50.dp)
                            .size(150.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Welcome, $username",
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }
    )
}

