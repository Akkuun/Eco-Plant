package com.akkuunamatata.eco_plant.database.plants.maps

import android.util.Log
import android.widget.Toast
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.log

class ParcelleRepository private constructor() {
    // Cache des parcelles déjà chargées
    private var parcelles: List<ParcelleData>? = null

    // État de chargement pour éviter les appels simultanés
    private var isLoading = false

    /**
     * Récupère toutes les parcelles, en utilisant le cache si disponible
     */
    suspend fun getAllParcelles(): List<ParcelleData> {
        // Si les données sont déjà en cache, les retourner immédiatement
        parcelles?.let { return it }

        // Si un chargement est déjà en cours, attendre qu'il se termine
        if (isLoading) {
            // Attendre que le chargement se termine
            while (isLoading) {
//                delay(100)
            }
            // Retourner les données du cache qui devraient maintenant être disponibles
            return parcelles ?: emptyList()
        }

        // Marquer comme en cours de chargement
        isLoading = true

        try {
            // Charger les données (depuis l'API, Firebase, etc.)
            val loadedParcelles = withContext(Dispatchers.IO) {
                fetchParcellesFromSource()
            }

            // Mettre en cache les données chargées
            parcelles = loadedParcelles
            return loadedParcelles
        } finally {
            // Marquer comme terminé, qu'il y ait eu succès ou erreur
            isLoading = false
        }
    }

    /**
     * Force le rechargement des données
     */
    suspend fun refreshParcelles() {
        parcelles = null
        getAllParcelles()
    }

    /**
     * Récupère les données depuis la source (API, Firebase, etc.)
     */
    private suspend fun fetchParcellesFromSource(): List<ParcelleData> {
        // Remplacer par votre logique de chargement réelle
        // Simuler un délai réseau
        //delay(1000)

        // the request -> loop of all user in users database
        // for each user, get  the plots collection to get the parcellData and inside it
        // loop on the plants collection to get the PlantSpecies
        var allPlots = mutableListOf<ParcelleData>()

        val db = FirebaseFirestore.getInstance()

        val usersSnapshot = db.collection("users").get().await()
        for (userDoc in usersSnapshot.documents) {
            val plants = mutableListOf<PlantSpecies>()
            val userId = userDoc.id
            val plotsSnapshot =
                db.collection("users").document(userId).collection("plots").get().await()

            for (plotDoc in plotsSnapshot.documents) {
                // log plotDoc data
                Log.d("PlotRepository", "Plot data: ${plotDoc.data}")
                val lat = plotDoc.getDouble("lat") ?: continue
                val long = plotDoc.getDouble("long") ?: continue
                val idAuthor = userId

                // get the plants collection inside of the plotDoc
                val plantsSnapshot =
                    db.collection("users").document(userId).collection("plots").document(plotDoc.id)
                        .collection("plants").get().await()

                for (plantDoc in plantsSnapshot.documents) {
                    // log plantDoc data
                    Log.d("PlotRepository", "Plant data: ${plantDoc.data}")
                    val plantName = plantDoc.getString("name") ?: continue
                    val services = plantDoc.get("services") as? List<Float> ?: continue
                    val reliabilities = plantDoc.get("reliabilities") as? List<Float> ?: continue
                    val culturalConditions =
                        plantDoc.get("culturalConditions") as? List<String> ?: continue

                    // Create PlantSpecies object
                    val plantSpecies = PlantSpecies(
                        name = plantName,
                        services = services.toFloatArray(),
                        reliabilities = reliabilities.toFloatArray(),
                        culturalConditions = culturalConditions.toTypedArray()
                    )
                    plants.add(plantSpecies)
                }
                // Create ParcelleData object
                val parcelleData = ParcelleData(
                    lat = lat,
                    long = long,
                    idAuthor = idAuthor,
                    plants = plants
                )
                allPlots.add(parcelleData)

            }
        }
        return allPlots
    }

    companion object {
        @Volatile
        private var instance: ParcelleRepository? = null

        fun getInstance(): ParcelleRepository {
            return instance ?: synchronized(this) {
                instance ?: ParcelleRepository().also { instance = it }
            }
        }
    }
}