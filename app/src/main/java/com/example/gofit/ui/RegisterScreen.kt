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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var lastName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isValidPhoneNumber: (String) -> Boolean = { phone ->
        phone.length == 10 && phone.startsWith("0") && phone.all { it.isDigit() }
    }

    val isValidPassword: (String) -> Boolean = { password ->
        password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    val doPasswordsMatch: (String, String) -> Boolean = { password, confirmPassword ->
        password == confirmPassword
    }

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
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            TextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
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
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone No.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, "")
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    when {
                        lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() ||
                                phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                        !isValidPhoneNumber(phoneNumber) -> {
                            Toast.makeText(context, "Phone number must start with '0' and be 10 digits long", Toast.LENGTH_SHORT).show()
                        }
                        !isValidPassword(password) -> {
                            Toast.makeText(context, "Password must be at least 8 characters, contain an uppercase letter, a lowercase letter, a digit, and a special character", Toast.LENGTH_LONG).show()
                        }
                        !doPasswordsMatch(password, confirmPassword) -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            db.collection("users")
                                .whereEqualTo("phoneNumber", phoneNumber)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (documents.isEmpty) {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val user = auth.currentUser
                                                    user?.sendEmailVerification()
                                                        ?.addOnCompleteListener { emailTask ->
                                                            if (emailTask.isSuccessful) {
                                                                val userInfo = hashMapOf(
                                                                    "lastName" to lastName,
                                                                    "firstName" to firstName,
                                                                    "email" to email,
                                                                    "phoneNumber" to phoneNumber
                                                                )
                                                                db.collection("users").document(user.uid).set(userInfo)
                                                                Toast.makeText(context, "Registration successful. Verification email sent to $email", Toast.LENGTH_LONG).show()
                                                                navController.navigate("login")
                                                            } else {
                                                                Toast.makeText(context, "Failed to send verification email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                } else {
                                                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(context, "Phone number already in use", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(context, "Error checking phone number: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Sign Up")
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
