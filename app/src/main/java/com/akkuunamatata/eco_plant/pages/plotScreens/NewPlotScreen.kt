package com.akkuunamatata.eco_plant.pages.plotScreens

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.pages.plantIdentificationScreens.getLocationAndUpdateText
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlotScreen(navController: NavHostController) {
    var locationText by remember { mutableStateOf("Position") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var plotName by remember { mutableStateOf("") }
    var personalNotes by remember { mutableStateOf("") }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                locationText = location
                latitude = lat
                longitude = lon
            }
        } else {
            locationText = "Permission refusée"
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
                getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                    locationText = location
                    latitude = lat
                    longitude = lon
                }
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.new_plot),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Widget GPS (copié de ScanScreen)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
                    modifier = Modifier.fillMaxSize(0.6f)
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
                            latitude = lat
                            longitude = lon
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

        OutlinedTextField(
            value = plotName,
            onValueChange = { plotName = it },
            label = { Text(stringResource(R.string.plot_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = personalNotes,
            onValueChange = { personalNotes = it },
            label = { Text(stringResource(R.string.personal_notes)) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    // Logique pour créer la parcelle
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                enabled = plotName.isNotBlank()
            ) {
                Text(stringResource(R.string.create))
            }
        }
    }
}