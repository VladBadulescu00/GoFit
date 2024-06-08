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
    val id: Int,
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

    private fun adjustForMedicalConditions(
        fitnessPlan: FitnessPlan,
        medicalConditions: String
    ): FitnessPlan {
        var adjustedFitnessPlan = fitnessPlan
        if (medicalConditions.contains("Heart Disease")) {
            adjustedFitnessPlan = adjustedFitnessPlan.copy(
                exercise = "Cardiovascular Exercises: Frog jumps, Burpees, Mountain climbers, Squat jumps, Jumping jacks to a step, Toe taps with jumps, Side to side jumping lunges, Prisoner squat jumps",
                duration = 30,
                caloriesBurned = 300
            )
        } else if (medicalConditions.contains("Hypertension")) {
            adjustedFitnessPlan = adjustedFitnessPlan.copy(
                exercise = "Aerobic Exercises: Jogging, Hiking, Bicycling, Swimming laps, Jumping rope, Aerobics, Weight lifting, Stair climbing",
                duration = (fitnessPlan.duration * 0.5).toInt(),
                caloriesBurned = (fitnessPlan.caloriesBurned * 0.5).toInt()
            )
        } else {
            adjustedFitnessPlan = adjustedFitnessPlan.copy(
                exercise = "Any Cardio Exercise: Running, Swimming, etc.",
                duration = fitnessPlan.duration * 2,
                caloriesBurned = fitnessPlan.caloriesBurned * 2
            )
        }
        return adjustedFitnessPlan
    }

    fun generateFitnessPlans(userId: String, medicalConditions: String) {
        viewModelScope.launch {
            val plans = InitialData.generateFitnessPlansForUser(userId)

            val adjustedPlans = plans.map { entity ->
                var fitnessPlan = FitnessPlan(
                    id = entity.id,
                    userId = entity.userId,
                    exercise = entity.exercise,
                    duration = entity.duration,
                    caloriesBurned = entity.caloriesBurned
                )

                if (fitnessPlan.exercise.contains("Cardio")) {
                    fitnessPlan = adjustForMedicalConditions(fitnessPlan, medicalConditions)
                }

                fitnessPlan
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

    fun saveFitnessPlan(fitnessPlan: FitnessPlan2, userId: String) {
        viewModelScope.launch {
            fitnessPlanDao.insert(
                FitnessPlanEntity(
                    userId = userId,
                    exercise = fitnessPlan.name,
                    duration = fitnessPlan.exercises.sumBy { calculateDuration(it) },
                    caloriesBurned = fitnessPlan.exercises.sumBy { calculateCaloriesBurned(it) }
                )
            )
        }
    }

    private fun calculateDuration(exercise: String): Int {
        // Add your logic to calculate duration based on exercise
        return 0
    }

    private fun calculateCaloriesBurned(exercise: String): Int {
        // Add your logic to calculate calories burned based on exercise
        return 0
    }
}
