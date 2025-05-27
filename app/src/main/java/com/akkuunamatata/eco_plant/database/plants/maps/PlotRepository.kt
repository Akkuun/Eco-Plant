package com.akkuunamatata.eco_plant.database.plants.maps

import android.util.Log
import com.akkuunamatata.eco_plant.database.plants.ParcelleData
import com.akkuunamatata.eco_plant.database.plants.PlantSpecies
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.Plot
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
            // Récupérer tous les utilisateurs
            val usersSnapshot = db.collection("users")
                .get()
                .await()

            // Pour chaque utilisateur
            for (userDoc in usersSnapshot.documents) {
                if (userDoc.id.isEmpty()) {
                    continue
                }

                val userId = userDoc.id
                val userName = userDoc.getString("name") ?: "Inconnu"


                // Récupérer les parcelles de l'utilisateur
                val plotsSnapshot = db.collection("users")
                    .document(userId)
                    .collection("plots").orderBy("lastEdited", Query.Direction.DESCENDING)
                    .get()
                    .await()

                // Skip if no plots
                if (plotsSnapshot.isEmpty) {
                    Log.d("PlotRepository", "Aucune parcelle trouvée pour l'utilisateur $userName")
                    continue
                }

                // Pour chaque parcelle de l'utilisateur
                for (plotDoc in plotsSnapshot.documents) {
                    // Récupérer directement le document de la parcelle pour accéder à tous ses champs
                    Log.d("PlotRepository", "PLOTDOC: ${plotDoc.data}")

                    // Extraire les coordonnées GPS de la parcelle
                    val latitude = plotDoc.getDouble("latitude")
                    val longitude = plotDoc.getDouble("longitude")


                    val plot = Plot(
                        id = plotDoc.id,
                        name = plotDoc.getString("name") ?: "",
                        lastEdited = (plotDoc.getTimestamp("lastEdited")?.toDate() ?: Date()),
                        location = plotDoc.getString("location") ?: "Emplacement inconnu"
                    )

                    Log.d("PlotRepository", "PLOT ${plot}")

                    val plantsForThisPlot = mutableListOf<PlantSpecies>()

                    val plantsSnapshot = db.collection("users")
                        .document(userId)
                        .collection("plots")
                        .document(plot.id)
                        .collection("plants")
                        .get()
                        .await()

                    // Traiter chaque document de plante dans la collection
                    for (plantDoc in plantsSnapshot.documents) {
                        Log.d("PlotRepository", "PLANTDOC ${plantDoc}")
                        try {
                            // Récupérer les données de la plante
                            val commonName = plantDoc.getString("commonName") ?: "Inconnu"
                            val scientificName = plantDoc.getString("scientificName") ?: "Inconnu"

                            // Récupérer les valeurs de service
                            val serviceValues = plantDoc.get("serviceValues") as? List<*>
                            val services = if (serviceValues != null && serviceValues.size >= 3) {
                                floatArrayOf(
                                    (serviceValues[0] as? Number)?.toFloat() ?: -1f,
                                    (serviceValues[1] as? Number)?.toFloat() ?: -1f,
                                    (serviceValues[2] as? Number)?.toFloat() ?: -1f
                                )
                            } else {
                                floatArrayOf(-1f, -1f, -1f)
                            }

                            // Récupérer les fiabilités
                            val reliabilities = (plantDoc.get("reliabilityValues") as? List<*>)?.map {
                                (it as? Number)?.toFloat() ?: -1f
                            }?.toFloatArray() ?: floatArrayOf(-1f, -1f, -1f)

                            // Récupérer les conditions culturales
                            val culturalConditions = (plantDoc.get("culturalConditions") as? List<*>)?.map {
                                it.toString()
                            }?.toTypedArray() ?: arrayOf("", "", "")

                            // Créer l'objet PlantSpecies et l'ajouter à la liste
                            val plantSpecies = PlantSpecies(
                                name = commonName,
                                services = services,
                                reliabilities = reliabilities,
                                culturalConditions = culturalConditions
                            )

                            plantsForThisPlot.add(plantSpecies)
                        } catch (e: Exception) {
                            Log.e("PlotRepository", "Erreur lors du traitement d'une plante: ${e.message}", e)
                        }
                    }

                    // Utiliser les coordonnées GPS récupérées directement du document
                    val parcelleData = ParcelleData(
                        lat = latitude ?: -1.0,
                        long = longitude ?: -1.0,
                        idAuthor = userName,
                        plants = plantsForThisPlot
                    )

                    allPlots.add(parcelleData)
                }
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