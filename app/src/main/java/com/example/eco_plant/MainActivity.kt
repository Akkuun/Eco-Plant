package com.example.eco_plant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.eco_plant.database.PlantDatabaseHelper
import com.example.eco_plant.ui.theme.EcoPlantTheme
import com.example.eco_plant.ui.theme.InterTypography
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val inputStream = assets.open("data-1744126677780.csv")
            val plantDatabaseHelper = PlantDatabaseHelper.createAsync(inputStream)
            // Use plants safely on the main thread here

            // Display the first 5 plants
            for (i in 0..4) {
                println(plantDatabaseHelper.plantSpecies[i].name)
                println("Services:")
                for (j in 0..2) {
                    println(plantDatabaseHelper.plantSpecies[i].services[j])
                }
                println("Reliabilities:")
                for (j in 0..2) {
                    println(plantDatabaseHelper.plantSpecies[i].reliabilities[j])
                }
                println("Cultural Conditions:")
                for (j in 0..2) {
                    println(plantDatabaseHelper.plantSpecies[i].culturalConditions[j])
                }
            }
        }




        setContent {
            EcoPlantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        style = InterTypography.displayLarge,
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  EcoPlantTheme {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Greeting("Android")
      }
  }
}