package com.akkuunamatata.eco_plant

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.navigation.AppNavHost
import com.akkuunamatata.eco_plant.ui.theme.EcoPlantTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var languageListenerRegistration: ListenerRegistration? = null
    private val languageState = mutableStateOf("fr") // Default language
    private var isInitialLoad = true // Flag to track initial load

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupLanguageListener()

        setContent {
            val currentLanguage by languageState

            EcoPlantTheme(dynamicColor = false) {
                // Using key parameter with language to force recomposition when language changes
                key(currentLanguage) {
                    AppNavigation() // Initialize the navigation system
                }
            }
        }
    }

    private fun setupLanguageListener() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Set up a real-time listener for language changes
        languageListenerRegistration = db.collection("users").document(userId)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null || documentSnapshot == null) return@addSnapshotListener

                val language = documentSnapshot.getString("lang") ?: "fr"

                if (isInitialLoad) {
                    // Only update the state on initial load without recreation
                    languageState.value = language
                    applyLanguageChange(language, false)
                    isInitialLoad = false
                } else if (language != languageState.value) {
                    // Only update and recreate if the language has actually changed
                    languageState.value = language
                    applyLanguageChange(language, true)
                }
            }
    }

    private fun applyLanguageChange(language: String, shouldRecreate: Boolean) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Only recreate if needed and explicitly requested
        if (shouldRecreate) {
            // Use a flag in intent to prevent multiple recreations
            val intent = intent
            intent.putExtra("language_just_changed", true)
            finish()
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reset the flag when coming from a recreation due to language change
        if (intent.getBooleanExtra("language_just_changed", false)) {
            intent.removeExtra("language_just_changed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the Firestore listener
        languageListenerRegistration?.remove()
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
                    popUpTo("map") { inclusive = true }
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