package com.akkuunamatata.eco_plant.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.utils.getLocationAndUpdateText
import com.google.android.gms.location.LocationServices

@Composable
fun LocationBar(
    onLocationUpdated: (String, Double?, Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var locationText by remember { mutableStateOf("Position") }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Gestion des permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                locationText = location
                onLocationUpdated(location, lat, lon)
            }
        } else {
            locationText = "Permission refusée"
            onLocationUpdated("Permission refusée", null, null)
        }
    }

    // Vérification des permissions au lancement
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
                getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                    locationText = location
                    onLocationUpdated(location, lat, lon)
                }
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // UI
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_map_filled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .graphicsLayer {
                        scaleX = 1.5f
                        scaleY = 1.5f
                    }
            )
        }

        Text(
            text = locationText,
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        Button(
            onClick = {
                if (hasLocationPermission) {
                    getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                        locationText = location
                        onLocationUpdated(location, lat, lon)
                    }
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxWidth(0.15f)
                .aspectRatio(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh_unfilled),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
    }
}