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
            ),
            MealPlanEntity(
                userId = userId,
                meal = "Dinner: Salmon with quinoa",
                calories = 500,
                protein = 35,
                carbs = 45,
                fats = 15,
                sodium = 300
            ),
            MealPlanEntity(
                userId = userId,
                meal = "Snack: Greek yogurt with nuts",
                calories = 200,
                protein = 10,
                carbs = 20,
                fats = 10,
                sodium = 150
            ),
            MealPlanEntity(
                userId = userId,
                meal = "Snack: Apple with peanut butter",
                calories = 150,
                protein = 5,
                carbs = 20,
                fats = 7,
                sodium = 100
            )
        )
    }

    fun generateFitnessPlansForUser(userId: String): List<FitnessPlanEntity> {
        return listOf(
            FitnessPlanEntity(
                userId = userId,
                exercise = "Push Exercises: Chest Press, Shoulder Press, Tricep Dips",
                duration = 45,
                caloriesBurned = 400
            ),
            FitnessPlanEntity(
                userId = userId,
                exercise = "Pull Exercises: Pull-ups, Bent-over Rows, Bicep Curls",
                duration = 45,
                caloriesBurned = 400
            ),
            FitnessPlanEntity(
                userId = userId,
                exercise = "Leg Exercises: Squats, Lunges, Deadlifts",
                duration = 45,
                caloriesBurned = 400
            ),
            FitnessPlanEntity(
                userId = userId,
                exercise = "Cardio: Running",
                duration = 30,
                caloriesBurned = 300
            )
        )
    }
}
