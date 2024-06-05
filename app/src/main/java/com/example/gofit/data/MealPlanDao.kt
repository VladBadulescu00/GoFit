package com.example.gofit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Insert
    suspend fun insert(mealPlan: MealPlanEntity): Long

    @Query("SELECT * FROM meal_plans")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>
}
