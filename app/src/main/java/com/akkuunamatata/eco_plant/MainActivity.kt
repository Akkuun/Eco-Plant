package com.akkuunamatata.eco_plant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.pages.*
import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme
import com.akkuunamatata.eco_plant.database.plants.PlantDatabaseHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Chargement async de la BDD
        lifecycleScope.launch {
            val db = PlantDatabaseHelper.getInstance(applicationContext)
            db.plantSpecies.take(5).forEach {
                println(it.name)
                println("Services: ${it.services.take(3)}")
                println("Reliabilities: ${it.reliabilities.take(3)}")
                println("Cultural Conditions: ${it.culturalConditions.take(3)}")
            }
        }

        setContent {
            EcoPlantTheme(dynamicColor = false) { // Use the custom theme
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        composable("map") { MapScreen() }
        composable("history") { HistoryScreen() }
        composable("scan") { ScanScreen() }
        composable("settings") { SettingsScreen(navController) }
        composable("sign_in") { SignInScreen() }
    }

    MainScreen(navController)
}

@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (selectedItem == 0) R.drawable.ic_map_filled else R.drawable.ic_map_unfilled
                            ),
                            contentDescription = stringResource(id = R.string.map)
                        )
                    },
                    label = { Text(stringResource(id = R.string.map)) },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("map")
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (selectedItem == 1) R.drawable.ic_history_filled else R.drawable.ic_history_unfilled
                            ),
                            contentDescription = stringResource(id = R.string.history)
                        )
                    },
                    label = { Text(stringResource(id = R.string.history)) },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("history")
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (selectedItem == 2) R.drawable.ic_plant_filled else R.drawable.ic_plant_unfilled
                            ),
                            contentDescription = stringResource(id = R.string.scan)
                        )
                    },
                    label = { Text(stringResource(id = R.string.scan)) },
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        navController.navigate("scan")
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (selectedItem == 3) R.drawable.ic_settings_filled else R.drawable.ic_settings_unfilled
                            ),
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    },
                    label = { Text(stringResource(id = R.string.settings)) },
                    selected = selectedItem == 3,
                    onClick = {
                        selectedItem = 3
                        navController.navigate("settings")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "map",
            modifier = Modifier.padding(innerPadding) // Appliquer le padding
        ) {
            composable("map") { MapScreen() }
            composable("history") { HistoryScreen() }
            composable("scan") { ScanScreen() }
            composable("settings") { SettingsScreen(navController) }
            composable("sign_in") { SignInScreen() }
        }
    }
}