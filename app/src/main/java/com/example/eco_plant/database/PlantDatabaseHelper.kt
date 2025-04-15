package com.example.eco_plant.database

import java.io.InputStream

class PlantDatabaseHelper(inputStream: InputStream) {
    var plantSpecies: List<PlantSpecies> = listOf()

    init {
        plantSpecies = readCSV(inputStream)
    }

    private fun readCSV(inputStream: InputStream): List<PlantSpecies> {
        val reader = inputStream.bufferedReader()
        val lines = reader.readLines()
        val speciesList = mutableListOf<PlantSpecies>()
        for (line in lines.drop(1)) { // Skip the first line (header)
            val values = line.split(";")
            if (values.size == 5) {
                val plant = values[1]
                if (speciesList.any { it.name == plant }) {
                    val existingSpecies = speciesList.find { it.name == plant }!!
                    val serviceIndex = when (values[0]) {
                        "nitrogen_provision" -> 0
                        "storage_and_return_water" -> 1
                        "soil_structuration" -> 2
                        else -> -1
                    }
                    if (serviceIndex != -1) {
                        existingSpecies.services[serviceIndex] = values[2].toFloat()
                        existingSpecies.reliabilities[serviceIndex] = values[3].toFloat()
                        existingSpecies.culturalConditions[serviceIndex] = values[4]
                    }
                } else {
                    val services = FloatArray(3)
                    val reliabilities = FloatArray(3)
                    val culturalConditions = arrayOf("", "", "")
                    val serviceIndex = when (values[0]) {
                        "nitrogen_provision" -> 0
                        "storage_and_return_water" -> 1
                        "soil_structuration" -> 2
                        else -> -1
                    }
                    if (serviceIndex != -1) {
                        services[serviceIndex] = values[2].toFloat()
                        reliabilities[serviceIndex] = values[3].toFloat()
                        culturalConditions[serviceIndex] = values[4]
                    }
                    speciesList.add(PlantSpecies(plant, services, reliabilities, culturalConditions))
                }
            }
        }
        return speciesList
    }
}