package com.example.gofit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class FitnessPlan(
    var id: String = "",  // Changed to var to allow reassignment
    val exercise: String = "",
    var duration: Int = 0,
    var caloriesBurned: Int = 0
)

class FitnessPlansViewModel(application: Application) : AndroidViewModel(application) {
    private val _fitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val fitnessPlans: StateFlow<List<FitnessPlan>> = _fitnessPlans

    private val _generatedFitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val generatedFitnessPlans: StateFlow<List<FitnessPlan>> = _generatedFitnessPlans

    private val _savedFitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val savedFitnessPlans: StateFlow<List<FitnessPlan>> = _savedFitnessPlans

    private val firestore = FirebaseFirestore.getInstance()

    fun generateFitnessPlans(userId: String, medicalConditions: String, userWeight: Float) {
        viewModelScope.launch {
            val fitnessPlans = mutableListOf<FitnessPlan>()
            val categories = listOf("lowerBody", "pull", "push", "cardio")

            for (category in categories) {
                val documents = firestore.collection("fitnessPlans")
                    .document(medicalConditions.lowercase(Locale.getDefault()))
                    .collection(category)
                    .limit(1)
                    .get()
                    .await()

                if (documents.isEmpty) {
                    Log.d("Firestore", "No fitness plans found for $category")
                } else {
                    for (doc in documents) {
                        val originalFitnessPlan = doc.toObject(FitnessPlan::class.java).apply { id = doc.id }
                        Log.d("FitnessPlansViewModel", "Original Fitness Plan: $originalFitnessPlan")
                        val adjustedFitnessPlan = adjustFitnessPlanForWeight(originalFitnessPlan, userWeight)
                        Log.d("FitnessPlansViewModel", "Adjusted Fitness Plan: $adjustedFitnessPlan")
                        fitnessPlans.add(adjustedFitnessPlan)
                    }
                }
            }

            _generatedFitnessPlans.value = fitnessPlans

            saveFitnessPlansToFirestore(userId, fitnessPlans)
        }
    }

    private fun saveFitnessPlansToFirestore(userId: String, fitnessPlans: List<FitnessPlan>) {
        viewModelScope.launch {
            val existingPlans = firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("fitnessPlans")
                .get()
                .await()

            val existingIds = existingPlans.documents.map { it.id }.toSet()

            fitnessPlans.filter { it.id !in existingIds }.forEach { fitnessPlan ->
                firestore.collection("userFitnessPlans")
                    .document(userId)
                    .collection("fitnessPlans")
                    .add(fitnessPlan)
                    .addOnSuccessListener { documentReference ->
                        val updatedFitnessPlan = fitnessPlan.copy(id = documentReference.id)
                        Log.d("Firestore", "Saved fitness plan to user collection: $updatedFitnessPlan")
                    }
            }
        }
    }

    private fun adjustFitnessPlanForWeight(fitnessPlan: FitnessPlan, userWeight: Float): FitnessPlan {
        val referenceWeight = 75f // Assume 75 kg as the reference weight
        val factor = userWeight / referenceWeight

        Log.d("FitnessPlansViewModel", "Adjusting Fitness Plan for Weight: Factor = $factor")

        return fitnessPlan.copy(
            caloriesBurned = (fitnessPlan.caloriesBurned * factor).toInt()
        )
    }

    fun fetchFitnessPlans(userId: String) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("fitnessPlans")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firestore", "No fitness plans found for user: $userId")
                    } else {
                        val plans = documents.map { doc ->
                            Log.d("Firestore", "Fetched fitness plan: ${doc.id} with data: ${doc.data}")
                            doc.toObject(FitnessPlan::class.java).apply { id = doc.id }
                        }
                        _savedFitnessPlans.value = plans
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching fitness plans for user: $userId", exception)
                }
        }
    }


    fun getSavedFitnessPlans(userId: String) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("fitnessPlans")
                .get()
                .addOnSuccessListener { documents ->
                    _savedFitnessPlans.value = documents.map { doc ->
                        val fitnessPlan = doc.toObject(FitnessPlan::class.java)
                        fitnessPlan.id = doc.id // Ensure the ID is set
                        fitnessPlan
                    }
                }
        }
    }

    fun deleteFitnessPlan(id: String, userId: String) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("fitnessPlans")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    getSavedFitnessPlans(userId) // Refresh the list
                }
        }
    }
}
