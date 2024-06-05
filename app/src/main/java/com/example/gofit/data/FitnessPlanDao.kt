package com.example.gofit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessPlanDao {

    @Insert
    suspend fun insert(fitnessPlan: FitnessPlanEntity): Long

    @Query("SELECT * FROM fitness_plans")
    fun getAllFitnessPlans(): Flow<List<FitnessPlanEntity>>
}
