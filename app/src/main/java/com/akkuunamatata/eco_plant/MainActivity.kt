package com.akkuunamatata.eco_plant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.navigation.AppNavHost
import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set the content view using Jetpack Compose
        setContent {
            EcoPlantTheme(dynamicColor = false) {
                AppNavigation() // Initialize the navigation system
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
    val selectedItem = remember { mutableStateOf(0) }

    // Observe the current destination to update the selected item
    val currentDestination by navController.currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    NavigationBar {
        // Map navigation item
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(
                        if (selectedItem.value == 0) R.drawable.ic_map_filled else R.drawable.ic_map_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.map)
                )
            },
            label = { Text(stringResource(id = R.string.map)) },
            selected = selectedItem.value == 0,
            onClick = {
                selectedItem.value = 0
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
                        if (selectedItem.value == 1) R.drawable.ic_history_filled else R.drawable.ic_history_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.history)
                )
            },
            label = { Text(stringResource(id = R.string.history)) },
            selected = selectedItem.value == 1,
            onClick = {
                selectedItem.value = 1
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
                        if (selectedItem.value == 2) R.drawable.ic_plant_filled else R.drawable.ic_plant_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.scan)
                )
            },
            label = { Text(stringResource(id = R.string.scan)) },
            selected = selectedItem.value == 2,
            onClick = {
                selectedItem.value = 2
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
                        if (selectedItem.value == 3) R.drawable.ic_settings_filled else R.drawable.ic_settings_unfilled
                    ),
                    contentDescription = stringResource(id = R.string.settings)
                )
            },
            label = { Text(stringResource(id = R.string.settings)) },
            selected = selectedItem.value == 3,
            onClick = {
                selectedItem.value = 3
                navController.navigate("settings") {
                    popUpTo("settings") { inclusive = true }
                }
            }
        )
    }
}