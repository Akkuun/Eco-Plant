package com.example.eco_plant.database

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class PlantDatabaseHelper private constructor(inputStream: InputStream) {
    var plantSpecies: List<PlantSpecies> = listOf()

    init {
        plantSpecies = readCSV(inputStream)
    }

    private fun readCSV(inputStream: InputStream): List<PlantSpecies> {
        val reader = inputStream.bufferedReader()
        val lines = reader.readLines()
        val speciesList = mutableListOf<PlantSpecies>()

        for (line in lines.drop(1)) {
            val values = line.split(";")
            if (values.size == 5) {
                val plant = values[1]
                val existing = speciesList.find { it.name == plant }
                val serviceIndex = when (values[0]) {
                    "nitrogen_provision" -> 0
                    "storage_and_return_water" -> 1
                    "soil_structuration" -> 2
                    else -> -1
                }
                if (serviceIndex != -1) {
                    if (existing != null) {
                        existing.services[serviceIndex] = values[2].toFloat()
                        existing.reliabilities[serviceIndex] = values[3].toFloat()
                        existing.culturalConditions[serviceIndex] = values[4]
                    } else {
                        val services = FloatArray(3)
                        val reliabilities = FloatArray(3)
                        val culturalConditions = arrayOf("", "", "")
                        services[serviceIndex] = values[2].toFloat()
                        reliabilities[serviceIndex] = values[3].toFloat()
                        culturalConditions[serviceIndex] = values[4]
                        speciesList.add(PlantSpecies(plant, services, reliabilities, culturalConditions))
                    }
                }
            }
        }
        return speciesList
    }

    companion object {
        @Volatile
        private var instance: PlantDatabaseHelper? = null

        suspend fun getInstance(context: Context): PlantDatabaseHelper {
            val existing = instance
            if (existing != null) return existing

            val newInstance = withContext(Dispatchers.IO) {
                val inputStream = context.assets.open("data-1744126677780.csv")
                PlantDatabaseHelper(inputStream)
            }

            synchronized(this) {
                if (instance == null) {
                    instance = newInstance
                }
            }

            return instance!!
        }
    }
}
