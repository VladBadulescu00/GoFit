package com.example.gofit.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gofit.ui.*
import com.example.gofit.viewmodel.MealPlansViewModel
import com.example.gofit.viewmodel.FitnessPlansViewModel

@Composable
fun Navigation(navController: NavHostController) {
    val mealPlansViewModel: MealPlansViewModel = viewModel()
    val fitnessPlansViewModel: FitnessPlansViewModel = viewModel()

    NavHost(navController = navController, startDestination = "authenticated_main") {
        composable("splash") { SplashScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("authenticated_main") { AuthenticatedMainScreen(navController) }
        composable("meal_plans") { MealPlansScreen(navController, mealPlansViewModel) }
        composable("my_plans") { MyPlansScreen(navController) }
        composable("nutrition_fitness") {  NutritionFitnessScreen(navController, mealPlansViewModel) }
        composable("my_progress") { MyProgressScreen(navController) }
        composable("saved_fitness_plans") { SavedFitnessPlansScreen(navController, fitnessPlansViewModel) }
        composable("saved_meal_plans") { SavedMealPlansScreen(navController, mealPlansViewModel) }
        }
}
