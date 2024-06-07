package com.example.gofit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gofit.data.AppDatabase
import com.example.gofit.data.InitialData
import com.example.gofit.data.MealPlanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MealPlan(
    val id: Int,
    val meal: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
    val sodium: Int
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

    private val database = Room.databaseBuilder(application, AppDatabase::class.java, "meal_plans.db")
        .fallbackToDestructiveMigration()
        .build()
    private val mealPlanDao = database.mealPlanDao()

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

            val plans = InitialData.generateMealPlansForUser(userId)

            val adjustedPlans = plans.map { entity ->
                MealPlan(
                    id = entity.id,
                    meal = entity.meal,
                    calories = entity.calories,
                    protein = entity.protein,
                    carbs = entity.carbs,
                    fats = entity.fats,
                    sodium = entity.sodium
                )
            }

            _generatedMealPlans.value = adjustedPlans

            // Save meal plans to the database
            adjustedPlans.forEach { mealPlan ->
                viewModelScope.launch {
                    mealPlanDao.insert(
                        MealPlanEntity(
                            userId = userId,
                            meal = mealPlan.meal,
                            calories = mealPlan.calories,
                            protein = mealPlan.protein,
                            carbs = mealPlan.carbs,
                            fats = mealPlan.fats,
                            sodium = mealPlan.sodium
                        )
                    )
                }
            }
        }
    }

    fun getSavedMealPlans(userId: String) {
        viewModelScope.launch {
            mealPlanDao.getMealPlansForUser(userId).collect { entities ->
                _savedMealPlans.value = entities.map { entity ->
                    MealPlan(
                        id = entity.id,
                        meal = entity.meal,
                        calories = entity.calories,
                        protein = entity.protein,
                        carbs = entity.carbs,
                        fats = entity.fats,
                        sodium = entity.sodium
                    )
                }
            }
        }
    }

    fun deleteMealPlan(id: Int, userId: String) {
        viewModelScope.launch {
            mealPlanDao.deleteMealPlanById(id)
            getSavedMealPlans(userId)
        }
    }
}
