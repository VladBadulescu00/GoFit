package com.example.gofit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class MealPlan(
    var id: String = "",  // Changed to var to allow reassignment
    val type: String = "",
    val meal: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val sodium: Int = 0,
    val fiber: Int = 0,  // Added fiber parameter
    var grams: Int = 100  // Added grams parameter
)

data class MealComponent(
    var id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val sodium: Int = 0,
    val fiber: Int = 0,  // Added fiber parameter
    var grams: Int = 100  // Added grams parameter
)


data class CustomMealPlan(
    var id: String = "",
    val userId: String = "",
    val name: String = "",
    val meals: List<MealComponent> = listOf()
)


class MealPlansViewModel(application: Application) : AndroidViewModel(application) {

    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans

    private val _generatedMealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val generatedMealPlans: StateFlow<List<MealPlan>> = _generatedMealPlans

    private val _dailyCaloricNeeds = MutableStateFlow<Int>(0)
    val dailyCaloricNeeds: StateFlow<Int> = _dailyCaloricNeeds

    private val _savedMealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val savedMealPlans: StateFlow<List<MealPlan>> = _savedMealPlans

    private val _customMealPlans = MutableStateFlow<List<CustomMealPlan>>(emptyList())
    val customMealPlans: StateFlow<List<CustomMealPlan>> = _customMealPlans

    private val _mealComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val mealComponents: StateFlow<List<MealComponent>> = _mealComponents

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _mainDishComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val mainDishComponents: StateFlow<List<MealComponent>> = _mainDishComponents

    private val _sideDishComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val sideDishComponents: StateFlow<List<MealComponent>> = _sideDishComponents

    private val _soupComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val soupComponents: StateFlow<List<MealComponent>> = _soupComponents

    private val _desertComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val desertComponents: StateFlow<List<MealComponent>> = _desertComponents

    private val _breakfastComponents = MutableStateFlow<List<MealComponent>>(emptyList())
    val breakfastComponents: StateFlow<List<MealComponent>> = _breakfastComponents

    private val _generatedMealPlansWithFactors = MutableStateFlow<List<Pair<MealPlan, Double>>>(emptyList())
    val generatedMealPlansWithFactors: StateFlow<List<Pair<MealPlan, Double>>> = _generatedMealPlansWithFactors

    private val _savedMealPlansWithFactors = MutableStateFlow<List<Pair<MealPlan, Double>>>(emptyList())
    val savedMealPlansWithFactors: StateFlow<List<Pair<MealPlan, Double>>> = _savedMealPlansWithFactors

    fun fetchMealComponentsForType(mealType: String, medicalCondition: String, subCategory: String) {
        viewModelScope.launch {
            val formattedMedicalCondition = when (medicalCondition) {
                "none" -> "none"
                "diabetes" -> "diabetes"
                "hypertension" -> "hypertension"
                else -> "heartDisease"
            }
            firestore.collection(mealType.lowercase())
                .document(formattedMedicalCondition)
                .collection(subCategory)
                .get()
                .addOnSuccessListener { documents ->
                    val components = documents.map { doc ->
                        doc.toObject(MealComponent::class.java)
                    }
                    when (subCategory) {
                        "mainDish" -> _mainDishComponents.value = components
                        "sideDish" -> _sideDishComponents.value = components
                        "soup" -> _soupComponents.value = components
                        "desert" -> _desertComponents.value = components
                        "meals" -> _breakfastComponents.value = components
                    }
                }
        }
    }

    private fun calculateCaloricNeeds(
        age: Int,
        weight: Float,
        height: Int,
        gender: String,
        activityLevel: String
    ): Int {
        val bmr = if (gender.lowercase() == "male") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }

        return (bmr * getActivityLevelMultiplier(activityLevel)).toInt()
    }

    private fun getActivityLevelMultiplier(activityLevel: String): Double {
        return when (activityLevel.lowercase(Locale.getDefault())) {
            "sedentary" -> 1.2
            "light" -> 1.375
            "moderate" -> 1.55
            "active" -> 1.725
            "very active" -> 1.9
            else -> 1.0
        }
    }


    fun generateMealPlans(
        age: Int,
        weight: Float,
        height: Int,
        gender: String,
        activityLevel: String,
        medicalConditions: String,
        userId: String
    ) {
        viewModelScope.launch {
            val caloricNeeds = calculateCaloricNeeds(age, weight, height, gender, activityLevel)
            _dailyCaloricNeeds.value = caloricNeeds

            Log.d("MealPlansViewModel", "Caloric Needs: $caloricNeeds")

            val mealPlansWithFactors = mutableListOf<Pair<MealPlan, Double>>()
            val categories = listOf("breakfast", "lunch", "diner")
            val formattedMedicalCondition = when (medicalConditions.lowercase(Locale.getDefault()).trim()) {
                "diabetes" -> "diabetes"
                "hypertension" -> "hypertension"
                "none" -> "none"
                else -> "heartDisease"
            }

            for (category in categories) {
                val documents = firestore.collection("mealPlans")
                    .document(formattedMedicalCondition)
                    .collection(category)
                    .limit(1)
                    .get()
                    .await()

                if (documents.isEmpty) {
                    Log.d("Firestore", "No meal plans found for $category")
                } else {
                    for (doc in documents) {
                        val originalMealPlan = doc.toObject(MealPlan::class.java).apply { id = doc.id }
                        Log.d("MealPlansViewModel", "Original Meal Plan: $originalMealPlan")
                        val (adjustedMealPlan, factor) = adjustMealPlanForCalories(originalMealPlan, caloricNeeds, category)
                        Log.d("MealPlansViewModel", "Adjusted Meal Plan: $adjustedMealPlan with Factor: $factor")
                        mealPlansWithFactors.add(Pair(adjustedMealPlan, factor))
                    }
                }
            }

            _generatedMealPlansWithFactors.value = mealPlansWithFactors

            saveMealPlansToFirestore(userId, mealPlansWithFactors.map { it.first })
        }
    }

    private fun saveMealPlansToFirestore(userId: String, mealPlans: List<MealPlan>) {
        viewModelScope.launch {
            val existingPlans = firestore.collection("userMealPlans")
                .document(userId)
                .collection("mealPlans")
                .get()
                .await()

            val existingIds = existingPlans.documents.map { it.id }.toSet()

            mealPlans.filter { it.id !in existingIds }.forEach { mealPlan ->
                firestore.collection("userMealPlans")
                    .document(userId)
                    .collection("mealPlans")
                    .add(mealPlan)
                    .addOnSuccessListener { documentReference ->
                        val updatedMealPlan = mealPlan.copy(id = documentReference.id)
                        Log.d("Firestore", "Saved meal plan to user collection: $updatedMealPlan")
                    }
            }
        }
    }

    fun getSavedMealPlans(userId: String) {
        viewModelScope.launch {
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("mealPlans")
                .get()
                .addOnSuccessListener { documents ->
                    val mealPlansWithFactors = documents.map { doc ->
                        val mealPlan = doc.toObject(MealPlan::class.java)
                        mealPlan.id = doc.id // Ensure the ID is set
                        val factor = adjustMealPlanForCalories(mealPlan, dailyCaloricNeeds.value, mealPlan.type).second
                        Pair(mealPlan, factor)
                    }
                    _savedMealPlansWithFactors.value = mealPlansWithFactors
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching saved meal plans", exception)
                }
        }
    }

    fun setDailyCaloricNeeds(age: Int, weight: Float, height: Int, gender: String, activityLevel: String) {
        _dailyCaloricNeeds.value = calculateCaloricNeeds(age, weight, height, gender, activityLevel)
    }

    fun getSavedMealPlansWithFactors(userId: String) {
        viewModelScope.launch {
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("mealPlans")
                .get()
                .addOnSuccessListener { documents ->
                    val mealPlansWithFactors = documents.map { doc ->
                        val mealPlan = doc.toObject(MealPlan::class.java)
                        mealPlan.id = doc.id // Ensure the ID is set
                        Log.d("MealPlansViewModel", "Fetched Meal Plan: $mealPlan")
                        val factor = adjustMealPlanForCalories(mealPlan, dailyCaloricNeeds.value, mealPlan.type).second
                        Log.d("MealPlansViewModel", "Factor for ${mealPlan.type}: $factor")
                        Pair(mealPlan, factor)
                    }
                    _savedMealPlansWithFactors.value = mealPlansWithFactors
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching saved meal plans", exception)
                }
        }
    }



    fun getCustomSavedMealPlans(userId: String) {
        viewModelScope.launch {
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("customMealPlans")
                .get()
                .addOnSuccessListener { documents ->
                    _customMealPlans.value = documents.map { doc ->
                        val customMealPlan = doc.toObject(CustomMealPlan::class.java)
                        customMealPlan.id = doc.id // Ensure the ID is set
                        customMealPlan
                    }
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching custom meal plans", exception)
                }
        }
    }

    fun adjustMealPlanForCalories(mealPlan: MealPlan, caloricNeeds: Int, category: String): Pair<MealPlan, Double> {
        val percentage = when (category.lowercase(Locale.getDefault())) {
            "breakfast" -> 0.25
            "lunch" -> 0.45
            "diner", "dinner" -> 0.30 // Ensure "diner" is used correctly
            else -> 0.0
        }

        val targetCalories = caloricNeeds * percentage
        val factor = if (mealPlan.calories != 0) targetCalories / mealPlan.calories else 0.0

        Log.d("MealPlansViewModel", "Adjusting Meal Plan for $category: Target Calories = $targetCalories, Factor = $factor")
        Log.d("MealPlansViewModel", "Meal Plan Before Adjustment: $mealPlan")

        val adjustedMealPlan = mealPlan.copy(
            calories = (mealPlan.calories * factor).toInt(),
            protein = (mealPlan.protein * factor).toInt(),
            carbs = (mealPlan.carbs * factor).toInt(),
            fats = (mealPlan.fats * factor).toInt(),
            sodium = (mealPlan.sodium * factor).toInt(),
            fiber = (mealPlan.fiber * factor).toInt(),
            grams = (mealPlan.grams * factor).toInt()
        )

        Log.d("MealPlansViewModel", "Adjusted Meal Plan: $adjustedMealPlan")

        return Pair(adjustedMealPlan, factor)
    }






    fun deleteMealPlan(id: String, userId: String) {
        viewModelScope.launch {
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("mealPlans")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    getSavedMealPlans(userId) // Refresh the list
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error deleting meal plan", exception)
                }
        }
    }

    fun deleteCustomMealPlan(id: String, userId: String) {
        viewModelScope.launch {
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("customMealPlans")
                .document(id)
                .delete()
                .addOnSuccessListener {
                    getCustomSavedMealPlans(userId) // Refresh the list
                }.addOnFailureListener { exception ->
                    Log.e("Firestore", "Error deleting custom meal plan", exception)
                }
        }
    }

    fun saveCustomMealPlan(mealPlan: CustomMealPlan) {
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userId = user.uid
                firestore.collection("userMealPlans")
                    .document(userId)
                    .collection("customMealPlans")
                    .add(mealPlan)
                    .addOnSuccessListener { documentReference ->
                        val updatedMealPlan = mealPlan.copy(id = documentReference.id)
                        Log.d("Firestore", "Saved custom meal plan to user collection: $updatedMealPlan")
                    }
            }
        }
    }

}
