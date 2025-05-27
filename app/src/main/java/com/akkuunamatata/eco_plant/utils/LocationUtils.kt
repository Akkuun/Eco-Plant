package com.akkuunamatata.eco_plant.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*

/**
 * Récupère la dernière position connue et convertit les coordonnées en nom de ville.
 * @param fusedLocationClient Le client de localisation à utiliser
 * @param context Le contexte Android
 * @param onLocationUpdated Callback appelé avec le nom de la localisation et les coordonnées
 */
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLocationAndUpdateText(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationUpdated: (String, Double?, Double?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (address?.isNotEmpty() == true) {
                onLocationUpdated(address[0].locality ?: "Ville inconnue", location.latitude, location.longitude)
            } else {
                onLocationUpdated("Lieu inconnu", location.latitude, location.longitude)
            }
        } else {
            onLocationUpdated("Localisation non disponible", null, null)
        }
    }
}

fun getActualGeoPosition(context: Context): GeoPoint? {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
    return try {
        // Get the last known location
        val location =
            locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                ?: return null // Return null if no location is found

        // Convert to GeoPoint
        GeoPoint(location.latitude, location.longitude)
    } catch (e: SecurityException) {
        e.printStackTrace()
        null // Return null if there is a security exception (e.g., permissions not granted)
    }
}

/**
 * Search for a location using the Nominatim API directly
 * @return Pair<Double, Double> containing latitude and longitude if found, null otherwise
 */
@SuppressLint("SetJavaScriptEnabled")
suspend fun searchLocationWithNominatim(query: String): Pair<Double, Double>? {
    return withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlString =
                "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&limit=1"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "EcoPlant/1.0 (Android)")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = connection.inputStream.bufferedReader()
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) {
                    val result = jsonArray.getJSONObject(0)
                    val lat = result.getDouble("lat")
                    val lon = result.getDouble("lon")
                    return@withContext Pair(lat, lon)
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
