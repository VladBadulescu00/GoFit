package com.example.gofit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gofit.ui.ForgotPasswordScreen
import com.example.gofit.ui.*

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("authenticated_main") { AuthenticatedMainScreen(navController) }
        composable("meal_plans") { MealPlansScreen(navController) }
        composable("my_plans") { MyPlansScreen(navController) }
        composable("nutrition_fitness") { NutritionFitnessScreen(navController) }
        composable("my_progress") { MyProgressScreen(navController) }
    }
}
