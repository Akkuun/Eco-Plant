package com.example.eco_plant.database

import java.io.InputStream

class PlantDatabaseHelper {
    var plantSpecies: List<PlantSpecies> = listOf()
    private var csvPath: String = ""

    constructor(
        csvPath: String
    ) {
        this.csvPath = csvPath
        plantSpecies = readCSV(javaClass.classLoader!!.getResourceAsStream(csvPath)!!)
    }

    // Create a function getSpecies that returns a PlantSpecies object from a string
    // The species is a parameter of type string taht should be searched in the csv column species
    // When matching the species with the asked services (nitrogen_provision, storage_and_return_water, soil_structuration) you need to :
    // - get the values of the services in column value and add it to the relevant cell of the PlantSpecies services array
    // - get the values of the reliabilities in column reliability and add it to the relevant cell of the PlantSpecies reliabilities array
    // - get the values of the cultural conditions in column cultural_conditions and add it to the relevant cell of the PlantSpecies culturalConditions array
    // The function should return a PlantSpecies object

    private fun readCSV(inputStream: InputStream): List<PlantSpecies> {
        val reader = inputStream.bufferedReader()
        val lines = reader.readLines()
        val speciesList = mutableListOf<PlantSpecies>()
        for (line in lines.drop(1)) { // Skip the first line (header)
            val values = line.split(";")
            if (values.size == 5) {
                val plant = values[1]
                // if plant already in speciesList, add the values to the existing PlantSpecies
                // else create it
                if (speciesList.any { it.name == plant }) {
                    val existingSpecies = speciesList.find { it.name == plant }!!
                    val serviceIndex = when (values[0]) {
                        "nitrogen_provision" -> 0
                        "storage_and_return_water" -> 1
                        "soil_structuration" -> 2
                        else -> -1 // err
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
                        else -> -1 // err
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