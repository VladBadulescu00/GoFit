package com.example.gofit.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gofit.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }
    var showResendEmailButton by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logogofit),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, "")
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    navController.navigate("authenticated_main")
                                } else {
                                    Toast.makeText(context, "Please verify your email address", Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                    showResendEmailButton = true
                                }
                            } else {
                                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Log In")
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (showResendEmailButton) {
                Button(
                    onClick = {
                        val user = auth.currentUser
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(context, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                                    showResendEmailButton = false
                                } else {
                                    Toast.makeText(context, "Failed to send verification email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "Resend Verification Email")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("forgot_password")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Forgot Password")
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
