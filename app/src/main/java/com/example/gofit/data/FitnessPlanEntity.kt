package com.example.gofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "fitness_plans")
data class FitnessPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "exercise") val exercise: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "caloriesBurned") val caloriesBurned: Int
)

