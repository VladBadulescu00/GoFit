package com.example.gofit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gofit.ui.*
import com.example.gofit.viewmodel.MealPlansViewModel
import com.example.gofit.viewmodel.FitnessPlansViewModel
import com.example.gofit.viewmodel.UserViewModel
import androidx.navigation.navArgument

@Composable
fun Navigation(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val mealPlansViewModel: MealPlansViewModel = viewModel()
    val fitnessPlansViewModel: FitnessPlansViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController, userViewModel) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("authenticated_main") {
            val userId = userViewModel.userId.collectAsState().value
            if (userId != null) {
                AuthenticatedMainScreen(navController, userId)
            }
        }
        composable("nutrition_fitness/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            NutritionFitnessScreen(
                navController,
                mealPlansViewModel,
                fitnessPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("generated_plans/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            GeneratedPlansScreen(
                navController,
                mealPlansViewModel,
                fitnessPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("custom_meal_plan/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            CustomMealPlanScreen(
                navController,
                mealPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("custom_fitness_plan/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            CustomFitnessPlanScreen(
                navController,
                fitnessPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("custom_plans/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            CustomPlansScreen(
                navController,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("my_progress") { MyProgressScreen(navController) }
        composable("saved_fitness_plans/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            SavedFitnessPlansScreen(
                navController,
                fitnessPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
        composable("saved_meal_plans/{userId}",
            arguments = listOf(navArgument("userId") { defaultValue = "" })
        ) { backStackEntry ->
            SavedMealPlansScreen(
                navController,
                mealPlansViewModel,
                backStackEntry.arguments?.getString("userId") ?: ""
            )
        }
    }
}
