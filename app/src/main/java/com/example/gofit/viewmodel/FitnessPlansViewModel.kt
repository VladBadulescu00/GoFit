package com.example.gofit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gofit.data.AppDatabase
import com.example.gofit.data.FitnessPlanEntity
import com.example.gofit.data.InitialData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FitnessPlan(
    val id: Int, // Add this line
    val userId: String,
    val exercise: String,
    val duration: Int,
    val caloriesBurned: Int
)

class FitnessPlansViewModel(application: Application) : AndroidViewModel(application) {
    private val _fitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val fitnessPlans: StateFlow<List<FitnessPlan>> = _fitnessPlans

    private val _generatedFitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val generatedFitnessPlans: StateFlow<List<FitnessPlan>> = _generatedFitnessPlans

    private val _savedFitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val savedFitnessPlans: StateFlow<List<FitnessPlan>> = _savedFitnessPlans

    private val database = Room.databaseBuilder(application, AppDatabase::class.java, "fitness_plans.db")
        .fallbackToDestructiveMigration()
        .build()
    private val fitnessPlanDao = database.fitnessPlanDao()

    fun generateFitnessPlans(userId: String) {
        viewModelScope.launch {
            val plans = InitialData.generateFitnessPlansForUser(userId)

            val adjustedPlans = plans.map { entity ->
                FitnessPlan(
                    id = entity.id,
                    userId = entity.userId,
                    exercise = entity.exercise,
                    duration = entity.duration,
                    caloriesBurned = entity.caloriesBurned
                )
            }

            _generatedFitnessPlans.value = adjustedPlans

            // Save fitness plans to the database
            adjustedPlans.forEach { fitnessPlan ->
                viewModelScope.launch {
                    fitnessPlanDao.insert(
                        FitnessPlanEntity(
                            userId = userId,
                            exercise = fitnessPlan.exercise,
                            duration = fitnessPlan.duration,
                            caloriesBurned = fitnessPlan.caloriesBurned
                        )
                    )
                }
            }
        }
    }

    fun getSavedFitnessPlans(userId: String) {
        viewModelScope.launch {
            fitnessPlanDao.getFitnessPlansForUser(userId).collect { entities ->
                _savedFitnessPlans.value = entities.map { entity ->
                    FitnessPlan(
                        id = entity.id,
                        userId = entity.userId,
                        exercise = entity.exercise,
                        duration = entity.duration,
                        caloriesBurned = entity.caloriesBurned
                    )
                }
            }
        }
    }

    fun deleteFitnessPlan(id: Int, userId: String) {
        viewModelScope.launch {
            fitnessPlanDao.deleteFitnessPlanById(id)
            getSavedFitnessPlans(userId)
        }
    }
}




