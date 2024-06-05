package com.example.gofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val meal: String,
    val calories: Int,
    val protein: Int, // in grams
    val carbs: Int, // in grams
    val fats: Int, // in grams
    val sodium: Int // in milligrams
)
