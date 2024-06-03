package com.example.gofit.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login")
                                } else {
                                    Toast.makeText(context, "Failed to send password reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please enter your email address", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Reset Password")
            }
        }
    }
}
