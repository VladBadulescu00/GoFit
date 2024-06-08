package com.example.gofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class MealPlan2(val id: Int, val name: String, val meals: List<String>)
data class FitnessPlan2(val id: Int, val name: String, val exercises: List<String>)

class MyPlansViewModel : ViewModel() {
    val myMealPlans: SnapshotStateList<MealPlan2> = mutableStateListOf()
    val myFitnessPlans: SnapshotStateList<FitnessPlan2> = mutableStateListOf()

    fun addMealPlan(plan: MealPlan2) {
        myMealPlans.add(plan)
    }

    fun editMealPlan(plan: MealPlan2) {
        val index = myMealPlans.indexOfFirst { it.id == plan.id }
        if (index != -1) {
            myMealPlans[index] = plan
        }
    }

    fun deleteMealPlan(planId: Int) {
        myMealPlans.removeAll { it.id == planId }
    }

    fun addFitnessPlan(plan: FitnessPlan2) {
        myFitnessPlans.add(plan)
    }

    fun editFitnessPlan(plan: FitnessPlan2) {
        val index = myFitnessPlans.indexOfFirst { it.id == plan.id }
        if (index != -1) {
            myFitnessPlans[index] = plan
        }
    }

    fun deleteFitnessPlan(planId: Int) {
        myFitnessPlans.removeAll { it.id == planId }
    }
}
