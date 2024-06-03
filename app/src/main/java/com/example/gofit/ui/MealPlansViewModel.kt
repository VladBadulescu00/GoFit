package com.example.gofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MealPlan(
    val meal: String,
    val calories: Int,
    val protein: Int, // in grams
    val carbs: Int, // in grams
    val fats: Int // in grams
)

class MealPlansViewModel : ViewModel() {
    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans

    fun generateMealPlans(
        age: Int,
        weight: Float,
        height: Int,
        gender: String,
        activityLevel: String,
        medicalConditions: String
    ) {
        viewModelScope.launch {
            // Example logic to generate meal plans based on the input data
            val plans = when (activityLevel) {
                "Sedentary" -> listOf(
                    MealPlan("Mic dejun: Iaurt cu fructe și cereale integrale", 250, 10, 35, 8),
                    MealPlan("Prânz: Salată de pui cu legume proaspete", 350, 25, 30, 15),
                    MealPlan("Cină: Pește la grătar cu legume la abur", 400, 30, 25, 18)
                )
                "Lightly active" -> listOf(
                    MealPlan("Mic dejun: Omletă cu legume și pâine integrală", 300, 20, 35, 12),
                    MealPlan("Prânz: Sandviș cu curcan și avocado", 400, 25, 40, 20),
                    MealPlan("Cină: Tocăniță de vită cu cartofi dulci", 500, 35, 45, 18)
                )
                "Moderately active" -> listOf(
                    MealPlan("Mic dejun: Smoothie cu banane, spanac și proteine", 350, 25, 50, 10),
                    MealPlan("Prânz: Quinoa cu legume și pui", 450, 30, 50, 15),
                    MealPlan("Cină: Paste integrale cu sos de roșii și carne de curcan", 550, 40, 60, 20)
                )
                "Very active" -> listOf(
                    MealPlan("Mic dejun: Fulgi de ovăz cu fructe și miere", 400, 20, 70, 10),
                    MealPlan("Prânz: Piept de pui cu orez brun și broccoli", 500, 40, 55, 15),
                    MealPlan("Cină: Somon la cuptor cu sparanghel", 600, 45, 30, 25)
                )
                "Super active" -> listOf(
                    MealPlan("Mic dejun: Clătite proteice cu fructe de pădure", 450, 30, 60, 15),
                    MealPlan("Prânz: Burrito cu fasole neagră și avocado", 550, 25, 65, 25),
                    MealPlan("Cină: Friptură de vită cu salată de spanac și nuci", 650, 50, 40, 30)
                )
                else -> listOf(
                    MealPlan("Mic dejun: Iaurt cu fructe și cereale integrale", 250, 10, 35, 8),
                    MealPlan("Prânz: Salată de pui cu legume proaspete", 350, 25, 30, 15),
                    MealPlan("Cină: Pește la grătar cu legume la abur", 400, 30, 25, 18)
                )
            }

            _mealPlans.value = plans
        }
    }
}
