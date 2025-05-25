package com.akkuunamatata.eco_plant.utils

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
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