package com.example.gofit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessPlanDao {
    @Insert
    suspend fun insert(fitnessPlan: FitnessPlanEntity): Long

    @Insert
    suspend fun insertAll(fitnessPlans: List<FitnessPlanEntity>)

    @Query("SELECT * FROM fitness_plans WHERE userId = :userId")
    fun getFitnessPlansForUser(userId: String): Flow<List<FitnessPlanEntity>>

    @Query("DELETE FROM fitness_plans WHERE id = :id")
    suspend fun deleteFitnessPlanById(id: Int)

}