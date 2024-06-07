package com.example.gofit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class MealPlan2(val id: Int, val name: String, val meals: List<String>)

class MyPlansViewModel : ViewModel() {
    val myPlans: SnapshotStateList<MealPlan2> = mutableStateListOf()

    fun addMealPlan(plan: MealPlan2) {
        myPlans.add(plan)
    }

    fun editMealPlan(plan: MealPlan2) {
        val index = myPlans.indexOfFirst { it.id == plan.id }
        if (index != -1) {
            myPlans[index] = plan
        }
    }

    fun deleteMealPlan(planId: Int) {
        myPlans.removeAll { it.id == planId }
    }
}
