package com.akkuunamatata.eco_plant.pages

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Configurer osmdroid
    DisposableEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        onDispose { }
    }

    // Créer et configurer la carte
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // Gérer le cycle de vie
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        ) { map ->
            // Configurer la position initiale (Paris)
            val startPoint = GeoPoint(48.8566, 2.3522)
            map.controller.setCenter(startPoint)

            // Ajouter un overlay de localisation
            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            locationOverlay.enableMyLocation()
            map.overlays.add(locationOverlay)
        }

        // Bouton pour centrer sur la position de l'utilisateur
        FloatingActionButton(
            onClick = {
                val locationOverlay = mapView.overlays
                    .filterIsInstance<MyLocationNewOverlay>()
                    .firstOrNull()

                locationOverlay?.let {
                    if (it.myLocation != null) {
                        mapView.controller.animateTo(it.myLocation)
                        mapView.controller.setZoom(18.0)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            // Vous devrez ajouter une icône appropriée ici
            // Icon(Icons.Default.MyLocation, contentDescription = "Ma position")
        }
    }
}