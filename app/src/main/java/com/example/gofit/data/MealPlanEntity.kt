package com.example.gofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "meal") val meal: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "protein") val protein: Int,
    @ColumnInfo(name = "carbs") val carbs: Int,
    @ColumnInfo(name = "fats") val fats: Int,
    @ColumnInfo(name = "sodium") val sodium: Int
)



