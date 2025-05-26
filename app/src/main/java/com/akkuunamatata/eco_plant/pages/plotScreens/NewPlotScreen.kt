package com.akkuunamatata.eco_plant.pages.plotScreens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.components.LocationBar
import com.akkuunamatata.eco_plant.utils.getLocationAndUpdateText
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlotScreen(navController: NavHostController) {
    var locationText by remember { mutableStateOf("Position") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var plotName by remember { mutableStateOf("") }
    var personalNotes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

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
        LocationBar(
            onLocationUpdated = { location, lat, lon ->
                locationText = location
                latitude = lat
                longitude = lon
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp)
        ) {
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(
                                onClick = { errorMessage = null },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("OK")
                            }
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                enabled = !isSaving
            ) {
                Text(stringResource(R.string.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (currentUser != null && latitude != null && longitude != null) {
                        isSaving = true
                        // Création de la parcelle dans Firebase
                        val plot = hashMapOf(
                            "name" to plotName,
                            "notes" to personalNotes,
                            "latitude" to latitude,
                            "longitude" to longitude,
                            "createdAt" to com.google.firebase.Timestamp.now(),
                            "lastEdited" to com.google.firebase.Timestamp.now(),
                        )

                        // Sauvegarde dans Firestore
                        db.collection("users")
                            .document(currentUser.uid)
                            .collection("plots")
                            .add(plot)
                            .addOnSuccessListener {
                                isSaving = false
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                isSaving = false
                                errorMessage = "Erreur lors de la sauvegarde: ${e.message}"
                            }
                    } else {
                        errorMessage = "Connexion ou localisation manquante"
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                enabled = plotName.isNotBlank() && !isSaving && latitude != null && longitude != null
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(stringResource(R.string.create))
                }
            }
        }
    }
}