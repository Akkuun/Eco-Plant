package com.example.eco_plant

import com.example.eco_plant.pages.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.eco_plant.ui.theme.EcoPlantTheme
import com.example.eco_plant.ui.theme.InterTypography
import com.example.eco_plant.database.PlantDatabaseHelper
import androidx.lifecycle.lifecycleScope
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
            EcoPlantTheme(dynamicColor = false){ // Forcer le thème sombre
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedItem by remember { mutableStateOf(2) }
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
                    onClick = { selectedItem = 0 }
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
                    onClick = { selectedItem = 1 }
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
                    onClick = { selectedItem = 2 }
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
                    onClick = { selectedItem = 3 }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (selectedItem) {
                0 -> MapScreen()
                1 -> HistoryScreen()
                2 -> ScanScreen()
                3 -> SettingsScreen()
            }
        }
    }
}


