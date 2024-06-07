package com.example.gofit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MealPlan(val meals: List<String>)

class NutritionViewModel : ViewModel() {

    private val _mealPlans = MutableStateFlow<MealPlan?>(null)
    val mealPlans: StateFlow<MealPlan?> = _mealPlans

    fun generateMealPlan(age: String, weight: String, height: String, gender: String, activityLevel: String, medicalConditions: String) {
        viewModelScope.launch {
            // Generate meal plans based on input data
            val meals = listOf("Meal 1", "Meal 2", "Meal 3") // Replace with actual logic
            _mealPlans.value = MealPlan(meals)
        }
    }
}