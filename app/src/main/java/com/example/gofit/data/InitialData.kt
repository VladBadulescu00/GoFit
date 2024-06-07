package com.example.gofit.data

object InitialData {

    fun generateMealPlansForUser(userId: String): List<MealPlanEntity> {
        return listOf(
            MealPlanEntity(
                userId = userId,
                meal = "Breakfast: Oatmeal with fruits",
                calories = 350,
                protein = 15,
                carbs = 50,
                fats = 10,
                sodium = 200
            ),
            MealPlanEntity(
                userId = userId,
                meal = "Lunch: Grilled chicken with vegetables",
                calories = 600,
                protein = 40,
                carbs = 45,
                fats = 20,
                sodium = 500
            )
        )
    }

    fun generateFitnessPlansForUser(userId: String): List<FitnessPlanEntity> {
        return listOf(
            FitnessPlanEntity(
                userId = userId,
                exercise = "Running",
                duration = 30,
                caloriesBurned = 300
            ),
            FitnessPlanEntity(
                userId = userId,
                exercise = "Cycling",
                duration = 45,
                caloriesBurned = 400
            )
        )
    }
}
