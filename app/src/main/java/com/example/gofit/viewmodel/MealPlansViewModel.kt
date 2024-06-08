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

            val initialMeals = InitialData.generateMealPlansForUser(userId)
            val meals = initialMeals.take(totalMeals)
            val snacks = initialMeals.drop(totalMeals).take(totalSnacks)

            val adjustedPlans = (meals + snacks).map { entity ->
                var mealPlan = MealPlan(
                    id = entity.id,
                    meal = entity.meal,
                    calories = if (entity.meal.contains("Snack")) snackCalories.toInt() else mealCalories.toInt(),
                    protein = (entity.protein * weight / 70).toInt(), // Assuming average weight 70kg for scaling
                    carbs = (entity.carbs * weight / 70).toInt(),
                    fats = (entity.fats * weight / 70).toInt(),
                    sodium = entity.sodium
                )

                mealPlan = adjustForMedicalConditions(mealPlan, medicalConditions)

                mealPlan
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

    fun saveMealPlan(mealPlan: MealPlan2, userId: String) {
        viewModelScope.launch {
            mealPlanDao.insert(
                MealPlanEntity(
                    userId = userId,
                    meal = mealPlan.name,
                    calories = mealPlan.meals.sumBy { calculateCalories(it) },
                    protein = mealPlan.meals.sumBy { calculateProtein(it) },
                    carbs = mealPlan.meals.sumBy { calculateCarbs(it) },
                    fats = mealPlan.meals.sumBy { calculateFats(it) },
                    sodium = mealPlan.meals.sumBy { calculateSodium(it) }
                )
            )
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
