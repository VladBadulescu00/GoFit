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

data class FitnessComponent(
    var id: String = "",
    val name: String = "",
    val calories: Int = 0
)

data class CustomFitnessPlan(
    var id: String = "",
    val userId: String = "",
    val name: String = "",
    val exercises: List<FitnessExercise> = listOf()
) {
    data class FitnessExercise(
        val name: String = "",
        val reps: Int = 0,
        val sets: Int = 0,
        val duration: Int = 0,
        val weight: Int = 0,
        val caloriesBurned: Int = 0
    )
}


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

    private val _customFitnessPlans = MutableStateFlow<List<CustomFitnessPlan>>(emptyList())
    val customFitnessPlans: StateFlow<List<CustomFitnessPlan>> = _customFitnessPlans

    private val _chestComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val chestComponents: StateFlow<List<FitnessComponent>> = _chestComponents

    private val _shouldersComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val shouldersComponents: StateFlow<List<FitnessComponent>> = _shouldersComponents

    private val _tricepsComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val tricepsComponents: StateFlow<List<FitnessComponent>> = _tricepsComponents

    private val _backComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val backComponents: StateFlow<List<FitnessComponent>> = _backComponents

    private val _bicepsComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val bicepsComponents: StateFlow<List<FitnessComponent>> = _bicepsComponents

    private val _absComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val absComponents: StateFlow<List<FitnessComponent>> = _absComponents

    private val _legsComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val legsComponents: StateFlow<List<FitnessComponent>> = _legsComponents

    private val _exerciseComponents = MutableStateFlow<List<FitnessComponent>>(emptyList())
    val exerciseComponents: StateFlow<List<FitnessComponent>> = _exerciseComponents

    private val firestore = FirebaseFirestore.getInstance()

    fun generateFitnessPlans(userId: String, medicalConditions: String, userWeight: Float) {
        viewModelScope.launch {
            val fitnessPlans = mutableListOf<FitnessPlan>()
            val categories = listOf("lowerBody", "pull", "push", "cardio")
            val formattedMedicalCondition = when (medicalConditions.lowercase(Locale.getDefault()).trim()) {
                "diabetes" -> "diabetes"
                "hypertension" -> "hypertension"
                "none" -> "none"
                else -> "heartDisease"
            }

            for (category in categories) {
                val path = "fitnessPlans/${formattedMedicalCondition}/$category"
                Log.d("FirestoreQuery", "Fetching from path: $path")

                val documents = firestore.collection("fitnessPlans")
                    .document(formattedMedicalCondition)
                    .collection(category)
                    .limit(1)
                    .get()
                    .await()

                if (documents.isEmpty) {
                    Log.d("Firestore", "No fitness plans found for $category at path: $path")
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
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching fitness plans for user: $userId", exception)
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
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting fitness plan", e)
                }
        }
    }

    fun fetchFitnessComponentsForType(type: String, medicalCondition: String, subCategory: String) {
        viewModelScope.launch {
            try {
                val path = "$type/$medicalCondition/$subCategory"
                Log.d("FitnessPlansViewModel", "Fetching data from path: $path")
                val snapshot = firestore.collection(path).get().await()
                val components = snapshot.documents.map { doc ->
                    doc.toObject(FitnessComponent::class.java)!!.apply { id = doc.id }
                }
                Log.d("FitnessPlansViewModel", "Fetched ${components.size} components for $subCategory")

                when (subCategory) {
                    "chest" -> _chestComponents.value = components
                    "shoulders" -> _shouldersComponents.value = components
                    "triceps" -> _tricepsComponents.value = components
                    "back" -> _backComponents.value = components
                    "biceps" -> _bicepsComponents.value = components
                    "abs" -> _absComponents.value = components
                    "legs" -> _legsComponents.value = components
                    "exercise" -> _exerciseComponents.value = components
                }
            } catch (e: Exception) {
                Log.e("FitnessPlansViewModel", "Error fetching fitness components", e)
            }
        }
    }

    fun saveCustomFitnessPlan(fitnessPlan: CustomFitnessPlan) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(fitnessPlan.userId)
                .collection("customFitnessPlans")
                .add(fitnessPlan)
                .addOnSuccessListener { documentReference ->
                    val updatedFitnessPlan = fitnessPlan.copy(id = documentReference.id)
                    _customFitnessPlans.value = _customFitnessPlans.value + updatedFitnessPlan
                    Log.d("Firestore", "Saved custom fitness plan to user collection: $updatedFitnessPlan")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error saving custom fitness plan", e)
                }
        }
    }

    fun getCustomSavedFitnessPlans(userId: String) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("customFitnessPlans")
                .get()
                .addOnSuccessListener { documents ->
                    _customFitnessPlans.value = documents.map { doc ->
                        val customFitnessPlan = doc.toObject(CustomFitnessPlan::class.java)
                        customFitnessPlan.id = doc.id // Ensure the ID is set
                        customFitnessPlan
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching custom fitness plans", e)
                }
        }
    }

    fun deleteCustomFitnessPlan(id: String, userId: String) {
        viewModelScope.launch {
            firestore.collection("userFitnessPlans")
                .document(userId)
                .collection("customFitnessPlans")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    _customFitnessPlans.value = _customFitnessPlans.value.filterNot { it.id == id }
                    Log.d("Firestore", "Deleted custom fitness plan with ID: $id")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting custom fitness plan", e)
                }
        }
    }
}
