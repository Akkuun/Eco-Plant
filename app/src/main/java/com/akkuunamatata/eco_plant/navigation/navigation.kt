package com.akkuunamatata.eco_plant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akkuunamatata.eco_plant.pages.*
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.OrganChoice
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.ScanScreen
import com.akkuunamatata.eco_plant.pages.userScreens.userSettingsScreens.*
import com.google.firebase.auth.FirebaseAuth
import androidx.core.net.toUri
import com.akkuunamatata.eco_plant.pages.userScreens.EmailVerificationScreen
import com.akkuunamatata.eco_plant.pages.userScreens.SettingsScreen
import com.akkuunamatata.eco_plant.pages.userScreens.SignInScreen
import com.akkuunamatata.eco_plant.pages.userScreens.UserChangeSettingsScreen

/**
 * Object containing all route constants for navigation.
 */
object Routes {
    const val MAP = "map"
    const val HISTORY = "history"
    const val SCAN = "scan"
    const val SETTINGS = "settings"
    const val ORGAN_CHOICE = "organ_choice"
}

/**
 * Main navigation host for the application.
 *
 * @param navController The navigation controller to manage navigation.
 * @param modifier Modifier to apply to the NavHost.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAP,
        modifier = modifier
    ) {
        // Map screen route
        composable(Routes.MAP) { MapScreen() }

        // History screen route
        composable(Routes.HISTORY) { HistoryScreen() }

        // Scan screen route
        composable(Routes.SCAN) { ScanScreen(navController) }

        // Settings screen route with conditional navigation
        composable(Routes.SETTINGS) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                UserChangeSettingsScreen(navController)
            } else {
                SettingsScreen(navController)
            }
        }

        // Settings detail routes
        addSettingsDetailRoutes(navController)


        // Sign-in screen route
        composable("sign_in") { SignInScreen(navController) }

        // Mail verification screen route
        composable("mailCheckup"){ EmailVerificationScreen(navController) }

        // Organ choice screen route with arguments
        composable(
            "${Routes.ORGAN_CHOICE}?imageUri={imageUri}&latitude={latitude}&longitude={longitude}&hasValidLocation={hasValidLocation}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.StringType; nullable = true },
                navArgument("longitude") { type = NavType.StringType; nullable = true },
                navArgument("hasValidLocation") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.toUri()
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
            val hasValidLocation = backStackEntry.arguments?.getBoolean("hasValidLocation") ?: false
            if (imageUri != null) {
                OrganChoice(navController, imageUri)
            }
        }
    }
}

/**
 * Adds all settings detail routes to the navigation graph.
 *
 * @param navController The navigation controller to manage navigation.
 */
private fun androidx.navigation.NavGraphBuilder.addSettingsDetailRoutes(navController: NavHostController) {
    composable("settingsDetail/ChangeUsername") { ChangeUsernameSettingsScreen(navController) }
    composable("settingsDetail/ChangePassword") { ChangePasswordSettingsScreen(navController) }
    composable("settingsDetail/ChangeEmail") { ChangeEmailSettingsScreen(navController) }
    composable("settingsDetail/lang") { ChangeLangageSettingsScreen(navController) }
    composable("settingsDetail/logout") { LogoutSettingsScreen(navController) }
    composable("settingsDetail/delete") { DeleteAccountSettingsScreen(navController) }
    composable("settingsDetail/switch") { SwitchAccountTheme(navController) }
}