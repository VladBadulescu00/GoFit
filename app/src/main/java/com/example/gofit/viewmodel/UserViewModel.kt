package com.example.gofit.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val firestore = FirebaseFirestore.getInstance()

    fun setUserId(userId: String) {
        _userId.value = userId
        // Debug log to check if userId is being set
        println("UserViewModel: setUserId called with userId = $userId")
    }

    // Method to create user document
    fun createUserDocument(userId: String) {
        val userDocument = firestore.collection("userMealPlans").document(userId)
        userDocument.get().addOnCompleteListener { task ->
            if (task.isSuccessful && !task.result.exists()) {
                userDocument.set(mapOf("userId" to userId))
            }
        }
    }
}
