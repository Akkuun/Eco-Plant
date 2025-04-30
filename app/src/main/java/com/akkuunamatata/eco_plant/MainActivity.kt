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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.navigation.AppNavHost
import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EcoPlantTheme(dynamicColor = false) { // Utilisation du thème personnalisé
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            MainBottomBar(navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun MainBottomBar(navController: androidx.navigation.NavHostController) {
    val selectedItem = remember { mutableStateOf(0) }
    val currentDestination by navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)

    NavigationBar {
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
                    popUpTo("map") { inclusive = true }
                }
            }
        )

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