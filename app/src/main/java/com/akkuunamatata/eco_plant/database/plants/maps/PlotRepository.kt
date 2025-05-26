package com.akkuunamatata.eco_plant.database.plants.maps

import android.util.Log
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.Plot
import com.akkuunamatata.eco_plant.utils.searchLocationWithNominatim
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class PlotRepository private constructor() {
    // Utilisation de StateFlow pour exposer les données et leur état
    private val _parcelles = MutableStateFlow<List<ParcelleData>>(emptyList())
    val parcelles: StateFlow<List<ParcelleData>> = _parcelles.asStateFlow()

    // Mutex pour la synchronisation des opérations de chargement
    private val mutex = Mutex()

    // Flag pour suivre si le chargement initial a été effectué
    private var isInitialized = false

    /**
     * Récupère toutes les parcelles, en utilisant le cache si disponible
     */
    suspend fun getAllParcelles(): List<ParcelleData> {
        // Utiliser le mutex pour éviter les chargements simultanés
        mutex.withLock {
            // Si déjà initialisé, retourner simplement les données actuelles
            if (isInitialized) {
                return _parcelles.value
            }

            try {
                // Charger les données depuis Firebase
                val loadedParcelles = withContext(Dispatchers.IO) {
                    fetchParcellesFromSource()
                }

                // Mettre à jour le StateFlow avec les nouvelles données
                _parcelles.value = loadedParcelles
                isInitialized = true

                return loadedParcelles
            } catch (e: Exception) {
                Log.e("PlotRepository", "Erreur lors de la récupération des parcelles", e)
                return emptyList()
            }
        }
    }

    /**
     * Force le rechargement des données
     */
    suspend fun refreshParcelles() {
        mutex.withLock {
            try {
                val loadedParcelles = withContext(Dispatchers.IO) {
                    fetchParcellesFromSource()
                }
                _parcelles.value = loadedParcelles
            } catch (e: Exception) {
                Log.e("PlotRepository", "Erreur lors du rafraîchissement des parcelles", e)
            }
        }
    }

    /**
     * Récupère les données depuis Firebase
     */
    private suspend fun fetchParcellesFromSource(): List<ParcelleData> {
        val allPlots = mutableListOf<ParcelleData>()
        val db = FirebaseFirestore.getInstance()

        try {
            // Le reste de votre code existant pour fetchParcellesFromSource
            // Récupérer tous les utilisateurs
            val usersSnapshot = db.collection("users")
                .get()
                .await()

            // Pour chaque utilisateur
            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                var lat = -1.0 // default value
                var long = -1.0 // default value
                val plants = mutableListOf<PlantSpecies>()
                // Récupérer les parcelles de l'utilisateur
                val plotsSnapshot = db.collection("users")
                    .document(userId)
                    .collection("plots").orderBy("lastEdited", Query.Direction.DESCENDING)
                    .get()
                    .await()
                Log.d(
                    "PlotRepository",
                    "Nombre de parcelles pour l'utilisateur $userId: ${plotsSnapshot.size()}"
                )
                val plots = plotsSnapshot.documents.map { doc ->
                    Plot(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        lastEdited = (doc.getTimestamp("lastEdited")?.toDate() ?: Date()),
                        location = doc.getString("location") ?: "Emplacement inconnu"
                    )
                }
                Log.d("PlotRepository", "Parcelles pour l'utilisateur $userId: $plots")
                var geoPoint = searchLocationWithNominatim(plots.firstOrNull()?.location ?: "Emplacement inconnu")

                allPlots.add(
                    ParcelleData(
                        lat = lat,
                        long = long,
                        idAuthor = userId,
                        plants = plants,
                    )
                )
                Log.d("PlotRepository", " ${geoPoint}")



            }
        } catch (e: Exception) {
            Log.e("PlotRepository", "Erreur lors de la récupération des parcelles", e)
        }

        return allPlots
    }

    companion object {
        @Volatile
        private var instance: PlotRepository? = null

        fun getInstance(): PlotRepository {
            return instance ?: synchronized(this) {
                instance ?: PlotRepository().also { instance = it }
            }
        }
    }
}