package com.example.gofit.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MealPlanEntity::class, FitnessPlanEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun fitnessPlanDao(): FitnessPlanDao
}
