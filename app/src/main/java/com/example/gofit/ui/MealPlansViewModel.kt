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
    val fats: Int, // in grams
    val sodium: Int // in milligrams
)

class MealPlansViewModel : ViewModel() {
    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans

    private val _dailyCaloricNeeds = MutableStateFlow<Int>(0)
    val dailyCaloricNeeds: StateFlow<Int> = _dailyCaloricNeeds

    private fun calculateCaloricNeeds(age: Int, weight: Float, height: Int, gender: String, activityLevel: String): Int {
        // Basic metabolic rate (BMR) calculation using Mifflin-St Jeor Equation
        val bmr = if (gender == "Male") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }

        // Activity factor
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

    fun generateMealPlans(
        age: Int,
        weight: Float,
        height: Int,
        gender: String,
        activityLevel: String,
        medicalConditions: String
    ) {
        viewModelScope.launch {
            val caloricNeeds = calculateCaloricNeeds(age, weight, height, gender, activityLevel)
            _dailyCaloricNeeds.value = caloricNeeds

            val plans = when (activityLevel) {
                "Sedentary" -> listOf(
                    MealPlan("Mic dejun: Iaurt cu fructe și cereale integrale", 212, 10, 35, 8, 100),
                    MealPlan("Prânz: Salată de pui cu legume proaspete", 297, 25, 30, 15, 200),
                    MealPlan("Cină: Pește la grătar cu legume la abur", 340, 30, 25, 18, 150),
                    MealPlan("Gustare: Măr și nuci", 170, 2, 30, 10, 50),
                    MealPlan("Gustare: Batoane de cereale", 150, 5, 20, 5, 100),
                    MealPlan("Gustare: Morcovi și hummus", 100, 2, 15, 7, 120)
                )
                "Lightly active" -> listOf(
                    MealPlan("Mic dejun: Omletă cu legume și pâine integrală", 320, 20, 35, 12, 150),
                    MealPlan("Prânz: Sandviș cu curcan și avocado", 420, 25, 40, 20, 200),
                    MealPlan("Cină: Tocăniță de vită cu cartofi dulci", 540, 35, 45, 18, 180),
                    MealPlan("Gustare: Smoothie cu banane și spanac", 250, 5, 45, 5, 50),
                    MealPlan("Gustare: Batoane proteice", 220, 20, 20, 8, 150),
                    MealPlan("Gustare: Migdale și iaurt grecesc", 180, 10, 10, 12, 70)
                )
                "Moderately active" -> listOf(
                    MealPlan("Mic dejun: Smoothie cu banane, spanac și proteine", 380, 25, 50, 10, 100),
                    MealPlan("Prânz: Quinoa cu legume și pui", 470, 30, 50, 15, 150),
                    MealPlan("Cină: Paste integrale cu sos de roșii și carne de curcan", 600, 40, 60, 20, 200),
                    MealPlan("Gustare: Iaurt grecesc cu miere și nuci", 320, 10, 20, 15, 100),
                    MealPlan("Gustare: Batoane proteice", 220, 20, 20, 8, 150),
                    MealPlan("Gustare: Smoothie cu căpșuni și lapte de migdale", 250, 15, 30, 5, 80)
                )
                "Very active" -> listOf(
                    MealPlan("Mic dejun: Fulgi de ovăz cu fructe și miere", 420, 20, 70, 10, 100),
                    MealPlan("Prânz: Piept de pui cu orez brun și broccoli", 520, 40, 55, 15, 150),
                    MealPlan("Cină: Somon la cuptor cu sparanghel", 620, 45, 30, 25, 200),
                    MealPlan("Gustare: Shake proteic cu lapte de migdale", 320, 30, 20, 10, 50),
                    MealPlan("Gustare: Batoane proteice", 240, 20, 25, 10, 150),
                    MealPlan("Gustare: Smoothie cu spanac și măr", 260, 10, 40, 5, 70)
                )
                "Super active" -> listOf(
                    MealPlan("Mic dejun: Clătite proteice cu fructe de pădure", 470, 30, 60, 15, 150),
                    MealPlan("Prânz: Burrito cu fasole neagră și avocado", 570, 25, 65, 25, 200),
                    MealPlan("Cină: Friptură de vită cu salată de spanac și nuci", 670, 50, 40, 30, 250),
                    MealPlan("Gustare: Batonaș proteic și un măr", 370, 20, 40, 10, 50),
                    MealPlan("Gustare: Iaurt grecesc cu miere și nuci", 320, 10, 20, 15, 100),
                    MealPlan("Gustare: Smoothie proteic cu unt de arahide", 350, 25, 30, 12, 80)
                )
                else -> listOf(
                    MealPlan("Mic dejun: Iaurt cu fructe și cereale integrale", 212, 10, 35, 8, 100),
                    MealPlan("Prânz: Salată de pui cu legume proaspete", 297, 25, 30, 15, 200),
                    MealPlan("Cină: Pește la grătar cu legume la abur", 340, 30, 25, 18, 150),
                    MealPlan("Gustare: Măr și nuci", 170, 2, 30, 10, 50),
                    MealPlan("Gustare: Batoane de cereale", 150, 5, 20, 5, 100),
                    MealPlan("Gustare: Morcovi și hummus", 100, 2, 15, 7, 120)
                )
            }

            // Adjust meal plans based on medical conditions and other factors
            val adjustedPlans = plans.map { mealPlan ->
                var adjustedMealPlan = mealPlan

                // Example adjustments based on medical conditions
                if (medicalConditions.contains("Diabetes", ignoreCase = true)) {
                    adjustedMealPlan = adjustedMealPlan.copy(carbs = (adjustedMealPlan.carbs * 0.9).toInt())
                }
                if (medicalConditions.contains("Hypertension", ignoreCase = true)) {
                    adjustedMealPlan = adjustedMealPlan.copy(sodium = (adjustedMealPlan.sodium * 0.8).toInt())
                }
                if (medicalConditions.contains("Heart Disease", ignoreCase = true)) {
                    adjustedMealPlan = adjustedMealPlan.copy(fats = (adjustedMealPlan.fats * 0.75).toInt())
                }

                // Further adjustments based on age, gender, and caloric needs
                if (age >= 50) {
                    adjustedMealPlan = adjustedMealPlan.copy(calories = (adjustedMealPlan.calories * 0.9).toInt())
                }
                if (gender.equals("Female", ignoreCase = true)) {
                    adjustedMealPlan = adjustedMealPlan.copy(calories = (adjustedMealPlan.calories * 0.85).toInt())
                }
                if (caloricNeeds > 2500) {
                    adjustedMealPlan = adjustedMealPlan.copy(protein = (adjustedMealPlan.protein * 1.1).toInt())
                }

                adjustedMealPlan
            }

            _mealPlans.value = adjustedPlans
        }
    }
}
