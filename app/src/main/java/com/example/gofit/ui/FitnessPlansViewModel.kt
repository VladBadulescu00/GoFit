package com.example.gofit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gofit.data.AppDatabase
import com.example.gofit.data.FitnessPlanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class FitnessPlan(
    val exercise: String,
    val duration: Int, // in minutes
    val caloriesBurned: Int
)

class FitnessPlansViewModel(application: Application) : AndroidViewModel(application) {
    private val _fitnessPlans = MutableStateFlow<List<FitnessPlan>>(emptyList())
    val fitnessPlans: StateFlow<List<FitnessPlan>> = _fitnessPlans

    private val database = Room.databaseBuilder(application, AppDatabase::class.java, "fitness_plans.db")
        .fallbackToDestructiveMigration()
        .build()
    private val fitnessPlanDao = database.fitnessPlanDao()

    fun generateFitnessPlans() {
        viewModelScope.launch {
            // Example fitness plans
            val plans = listOf(
                FitnessPlan("Jogging", 30, 300),
                FitnessPlan("Cycling", 45, 400),
                FitnessPlan("Swimming", 60, 500)
            )

            _fitnessPlans.value = plans

            // Save fitness plans to the database
            plans.forEach { fitnessPlan ->
                viewModelScope.launch {
                    fitnessPlanDao.insert(
                        FitnessPlanEntity(
                            exercise = fitnessPlan.exercise,
                            duration = fitnessPlan.duration,
                            caloriesBurned = fitnessPlan.caloriesBurned
                        )
                    )
                }
            }
        }
    }

    fun getSavedFitnessPlans() {
        viewModelScope.launch {
            fitnessPlanDao.getAllFitnessPlans().collect { entities ->
                _fitnessPlans.value = entities.map { entity ->
                    FitnessPlan(
                        exercise = entity.exercise,
                        duration = entity.duration,
                        caloriesBurned = entity.caloriesBurned
                    )
                }
            }
        }
    }
}
