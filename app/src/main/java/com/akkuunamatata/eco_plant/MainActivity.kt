package com.akkuunamatata.eco_plant

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.navigation.AppNavHost
import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setAppLanguage()
        // Set the content view using Jetpack Compose
        setContent {
            EcoPlantTheme(dynamicColor = false) {
                AppNavigation() // Initialize the navigation system
            }
        }
    }

    private fun setAppLanguage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                val language = document.getString("lang") ?: "fr" // Default to French if not set
                val locale = Locale(language)
                Locale.setDefault(locale)

                val config = Configuration()
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
    }

}

/**
 * Main composable function that sets up the navigation and scaffold layout.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            MainBottomBar(navController) // Define the bottom navigation bar
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding) // Apply padding from the scaffold
        )
    }
}

/**
 * Composable function for the bottom navigation bar with 4 navigation items.
 */
@Composable
fun MainBottomBar(navController: NavHostController) {
    val selectedItem = remember { mutableIntStateOf(0) }



    NavigationBar {
        // Map navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        if (selectedItem.intValue == 0) R.drawable.ic_map_filled else R.drawable.ic_map_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.map)
                )
            },
            label = { Text(stringResource(id = R.string.map)) },
            selected = selectedItem.intValue == 0,
            onClick = {
                selectedItem.intValue = 0
                navController.navigate("map") {
                    popUpTo("map") { inclusive = true } // Prevent multiple instances
                }
            }
        )

        // History navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        if (selectedItem.intValue == 1) R.drawable.ic_history_filled else R.drawable.ic_history_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.history)
                )
            },
            label = { Text(stringResource(id = R.string.history)) },
            selected = selectedItem.intValue == 1,
            onClick = {
                selectedItem.intValue = 1
                navController.navigate("history") {
                    popUpTo("history") { inclusive = true }
                }
            }
        )

        // Scan navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        if (selectedItem.intValue == 2) R.drawable.ic_plant_filled else R.drawable.ic_plant_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.scan)
                )
            },
            label = { Text(stringResource(id = R.string.scan)) },
            selected = selectedItem.intValue == 2,
            onClick = {
                selectedItem.intValue = 2
                navController.navigate("scan") {
                    popUpTo("scan") { inclusive = true }
                }
            }
        )

        // Settings navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        if (selectedItem.intValue == 3) R.drawable.ic_settings_filled else R.drawable.ic_settings_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.settings)
                )
            },
            label = { Text(stringResource(id = R.string.settings)) },
            selected = selectedItem.intValue == 3,
            onClick = {
                selectedItem.intValue = 3
                navController.navigate("settings") {
                    popUpTo("settings") { inclusive = true }
                }
            }
        )
    }
}