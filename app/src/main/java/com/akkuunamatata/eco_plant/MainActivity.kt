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
        import androidx.navigation.compose.NavHost
        import androidx.navigation.compose.composable
        import androidx.navigation.compose.rememberNavController
        import com.akkuunamatata.eco_plant.pages.*
        import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme
        import com.akkuunamatata.eco_plant.pages.userScreens.EmailVerificationScreen
        import com.akkuunamatata.eco_plant.pages.userScreens.SettingsScreen
        import com.akkuunamatata.eco_plant.pages.userScreens.SignInScreen

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
                NavHost(
                    navController = navController,
                    startDestination = "map",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("map") { MapScreen() }
                    composable("history") { HistoryScreen() }
                    composable("scan") { ScanScreen() }
                    composable("settings") { SettingsScreen(navController) }
                    composable("sign_in") { SignInScreen(navController) }
                    composable("mailCheckup") { EmailVerificationScreen(navController) }
                }
            }
        }

        @Composable
        fun MainBottomBar(navController: androidx.navigation.NavHostController) {
            var selectedItem by remember { mutableStateOf(0) }

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
                        navController.navigate("map") {
                            popUpTo("map") { inclusive = true }
                        }
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
                        navController.navigate("history") {
                            popUpTo("history") { inclusive = true }
                        }
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
                        navController.navigate("scan") {
                            popUpTo("scan") { inclusive = true }
                        }
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
                        navController.navigate("settings") {
                            popUpTo("settings") { inclusive = true }
                        }
                    }
                )
            }
        }