package com.akkuunamatata.eco_plant.utils

class PlantNetKey {
    companion object {
        private const val KEY = PlantNetConfig.PLANTNET_API_KEY

        fun getApiKey(): String {
            return KEY
        }
    }
}