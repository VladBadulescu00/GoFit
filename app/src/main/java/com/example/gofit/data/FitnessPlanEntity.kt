package com.example.gofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_plans")
data class FitnessPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exercise: String,
    val duration: Int, // in minutes
    val caloriesBurned: Int
)
