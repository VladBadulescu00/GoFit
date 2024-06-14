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
import java.util.Locale

data class MealPlan(
    var id: String = "",  // Changed to var to allow reassignment
    val type: String = "",
    val meal: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val sodium: Int = 0
)

data class MealComponent(
    var id: String = "",
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val sodium: Int = 0
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

    fun fetchMealComponentsForType(mealType: String, medicalCondition: String, subCategory: String) {
        val formattedMealType = mealType.toLowerCase(Locale.ROOT) // Converts to lowercase
        val formattedMedicalCondition = medicalCondition.decapitalize(Locale.ROOT) // Corrects capitalization if needed
        val formattedSubCategory = subCategory.decapitalize(Locale.ROOT) // Corrects capitalization if needed

        viewModelScope.launch {
            firestore.collection(formattedMealType)
                .document(formattedMedicalCondition)
                .collection(formattedSubCategory)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.d("Firestore", "No components found at path: $formattedMealType/$formattedMedicalCondition/$formattedSubCategory")
                    } else {
                        val components = documents.map { doc ->
                            Log.d("Firestore", "Fetched component: ${doc.id} with data: ${doc.data}")
                            doc.toObject(MealComponent::class.java)
                        }
                        _mealComponents.value = components
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching meal components at $formattedMealType/$formattedMedicalCondition/$formattedSubCategory", exception)
                }
        }
    }




    private fun calculateCaloricNeeds(age: Int, weight: Float, height: Int, gender: String, activityLevel: String): Int {
        val bmr = if (gender == "Male") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
        val activityFactor = when (activityLevel) {
            "Sedentary" -> 1.2
            "Lightly active" -> 1.375
            "Moderately active" -> 1.55
            "Very active" -> 1.725
            "Super active" -> 1.9
            else -> 1.2
        }
        return (bmr * activityFactor).toInt()
    }

    private fun adjustForMedicalConditions(
        mealPlan: MealPlan,
        medicalConditions: String
    ): MealPlan {
        var adjustedMealPlan = mealPlan
        if (medicalConditions.contains("Diabetes")) {
            adjustedMealPlan = adjustedMealPlan.copy(carbs = (mealPlan.carbs * 0.8).toInt())
        }
        if (medicalConditions.contains("Hypertension")) {
            adjustedMealPlan = adjustedMealPlan.copy(sodium = (mealPlan.sodium * 0.7).toInt())
        }
        if (medicalConditions.contains("Heart Disease")) {
            adjustedMealPlan = adjustedMealPlan.copy(fats = (mealPlan.fats * 0.8).toInt())
        }
        return adjustedMealPlan
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

            val totalMeals = 3
            val totalSnacks = 2
            val mealCalories = caloricNeeds * 0.7 / totalMeals
            val snackCalories = caloricNeeds * 0.3 / totalSnacks

            firestore.collection("mealPlans").get().addOnSuccessListener { documents ->
                val initialMeals = documents.map { doc ->
                    doc.toObject(MealPlan::class.java)
                }
                val meals = initialMeals.take(totalMeals)
                val snacks = initialMeals.drop(totalMeals).take(totalSnacks)

                val adjustedPlans = (meals + snacks).map { entity ->
                    var mealPlan = MealPlan(
                        id = entity.id,
                        type = entity.type,
                        meal = entity.meal,
                        calories = if (entity.type.contains("Snack")) snackCalories.toInt() else mealCalories.toInt(),
                        protein = (entity.protein * weight / 70).toInt(),
                        carbs = (entity.carbs * weight / 70).toInt(),
                        fats = (entity.fats * weight / 70).toInt(),
                        sodium = entity.sodium
                    )

                    mealPlan = adjustForMedicalConditions(mealPlan, medicalConditions)

                    mealPlan
                }

                _generatedMealPlans.value = adjustedPlans

                // Save meal plans to user-specific Firestore collection
                adjustedPlans.forEach { mealPlan ->
                    viewModelScope.launch {
                        firestore.collection("userMealPlans")
                            .document(userId)
                            .collection("mealPlans")
                            .add(mealPlan)
                            .addOnSuccessListener { documentReference ->
                                // Create a new instance with the updated ID
                                val updatedMealPlan = mealPlan.copy(id = documentReference.id)
                                // You can now update the meal plan in the list if needed
                            }
                    }
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
                    _savedMealPlans.value = documents.map { doc ->
                        val mealPlan = doc.toObject(MealPlan::class.java)
                        mealPlan.id = doc.id // Ensure the ID is set
                        mealPlan
                    }
                }
        }
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
                }
        }
    }

    fun saveCustomMealPlan(mealPlan: CustomMealPlan) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            firestore.collection("userMealPlans")
                .document(userId)
                .collection("customMealPlans")
                .add(mealPlan)
                .addOnSuccessListener { documentReference ->
                    val updatedMealPlan = mealPlan.copy(id = documentReference.id)
                    // Optionally update the state here if needed
                }
        }
    }

    private fun calculateCalories(meal: String): Int {
        // Add your logic to calculate calories based on meal
        return 0
    }

    private fun calculateProtein(meal: String): Int {
        // Add your logic to calculate protein based on meal
        return 0
    }

    private fun calculateCarbs(meal: String): Int {
        // Add your logic to calculate carbs based on meal
        return 0
    }

    private fun calculateFats(meal: String): Int {
        // Add your logic to calculate fats based on meal
        return 0
    }

    private fun calculateSodium(meal: String): Int {
        // Add your logic to calculate sodium based on meal
        return 0
    }
}
