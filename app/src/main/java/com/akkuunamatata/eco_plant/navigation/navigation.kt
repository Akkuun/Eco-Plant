package com.akkuunamatata.eco_plant.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.akkuunamatata.eco_plant.pages.HistoryScreen
import com.akkuunamatata.eco_plant.pages.MapScreen
import com.akkuunamatata.eco_plant.pages.OrganChoice
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.ScanScreen
import com.akkuunamatata.eco_plant.pages.userScreens.DeleteAccountSettings
import com.akkuunamatata.eco_plant.pages.userScreens.EmailSettings
import com.akkuunamatata.eco_plant.pages.userScreens.EmailVerificationScreen
import com.akkuunamatata.eco_plant.pages.userScreens.LanguageSettings
import com.akkuunamatata.eco_plant.pages.userScreens.LogoutSettings
import com.akkuunamatata.eco_plant.pages.userScreens.PasswordSettings
import com.akkuunamatata.eco_plant.pages.userScreens.SettingsScreen
import com.akkuunamatata.eco_plant.pages.userScreens.SignInScreen
import com.akkuunamatata.eco_plant.pages.userScreens.UserPageScreen
import com.akkuunamatata.eco_plant.pages.userScreens.UsernameSettings
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val MAP = "map"
    const val HISTORY = "history"
    const val SCAN = "scan"
    const val SETTINGS = "settings"
    const val SETTINGS_LOGGED = "settingsLogged"
}

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
        composable(Routes.MAP) { MapScreen() }
        composable(Routes.HISTORY) { HistoryScreen() }
        composable(Routes.SCAN) { ScanScreen(navController) }
        composable(Routes.SETTINGS) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                UserPageScreen(navController)
            } else {
                SettingsScreen(navController)
            }
        }

        composable("settingsDetail/ChangeUsername") { UsernameSettings(navController) }
        composable("settingsDetail/ChangePassword") { PasswordSettings( navController) }
        composable("settingsDetail/ChangeEmail") { EmailSettings(navController) }
        composable("settingsDetail/lang") { LanguageSettings(navController) }
        composable("settingsDetail/logout") { LogoutSettings(navController) }
        composable("settingsDetail/delete"){ DeleteAccountSettings(navController) }
        composable("emailVerification") { EmailVerificationScreen(navController) }
        composable("sign_in"){ SignInScreen(navController) }
        composable(
            "organ_choice?imageUri={imageUri}&latitude={latitude}&longitude={longitude}&hasValidLocation={hasValidLocation}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.StringType; nullable = true },
                navArgument("longitude") { type = NavType.StringType; nullable = true },
                navArgument("hasValidLocation") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val imageUri = Uri.parse(backStackEntry.arguments?.getString("imageUri"))
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
            val hasValidLocation = backStackEntry.arguments?.getBoolean("hasValidLocation") ?: false
            OrganChoice(navController, imageUri, latitude, longitude, hasValidLocation)
        }
    }
}