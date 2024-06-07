package com.example.gofit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Insert
    suspend fun insert(mealPlan: MealPlanEntity): Long

    @Insert
    suspend fun insertAll(mealPlans: List<MealPlanEntity>)

    @Query("SELECT * FROM meal_plans")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>

    @Query("SELECT COUNT(*) FROM meal_plans")
    suspend fun getAllMealPlansCount(): Int

    @Query("SELECT * FROM meal_plans WHERE userId = :userId")
    fun getMealPlansForUser(userId: String): Flow<List<MealPlanEntity>>

    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteMealPlanById(id: Int)
}
