package com.example.gofit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gofit.R

@Composable
fun SplashScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logogofit),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Register")
            }
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Log In")
            }
        }
    }
}

