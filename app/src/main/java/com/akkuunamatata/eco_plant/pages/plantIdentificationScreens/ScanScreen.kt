package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Environment
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


@Composable
fun ScanScreen(navController: androidx.navigation.NavHostController) {
    var locationText by remember { mutableStateOf("Position") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }

    // Function to create a file to save the image
    fun createImageFile(): File {
        val imageDir = context.cacheDir
        val imageFile = File(imageDir, "photo_${System.currentTimeMillis()}.jpg")
        return imageFile
    }

    // Gestion des permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasLocationPermission = granted
        if (granted) {
            getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                locationText = location
                latitude = lat
                longitude = lon
            }
        } else {
            locationText = "Permission Denied"
        }
    }




    fun saveBitmapToEcoPlantFolder(context: Context, bitmap: Bitmap): Uri? {
        // Prepare the content values to insert into the MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "eco_plant_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EcoPlant")
        }

        // Insert the content values into MediaStore to get the URI
        val contentResolver = context.contentResolver
        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        // Write the bitmap to the URI if it's valid
        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }

        return imageUri
    }


    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            // After image is captured, load the bitmap from the URI
            val uri = imageUri.value
            if (uri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                bitmapImage = bitmap

                val savedUri = saveBitmapToEcoPlantFolder(context, bitmap)

                if (savedUri != null) {
                    navController.navigateToOrganChoice(
                        savedUri,
                        latitude,
                        longitude,
                        hasLocationPermission
                    )
                }
            }
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val photoFile = createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            imageUri.value = uri
            cameraLauncher.launch(uri)
        }
    }

    // Lancer la galerie
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            navController.navigateToOrganChoice(it, latitude, longitude, hasLocationPermission)
        }
    }



    // Check permission on first launch
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
                getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                    locationText = location
                    latitude = lat
                    longitude = lon
                }
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // GPS Position row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Position icon
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

                // Position text
                Text(
                    text = locationText,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )

                // Refresh button
                Button(
                    onClick = {
                        if (hasLocationPermission) {
                            getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                                locationText = location
                                latitude = lat
                                longitude = lon
                                android.util.Log.d("ScanScreen", "Location updated: $locationText ($lat, $lon)")
                            }
                        } else {
                            android.util.Log.d("ScanScreen", "Location permission not granted")
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
                        tint = MaterialTheme.colorScheme.inverseSurface,
                        modifier = Modifier.fillMaxSize(0.6f)
                    )
                }
            }

            // Centered elements
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = stringResource(id = R.string.scan_title),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Gallery and Camera buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery button
                        Box(
                            modifier = Modifier
                                .weight(0.2f),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = CircleShape,
                                contentPadding = PaddingValues(5.dp),
                                modifier = Modifier
                                    .aspectRatio(1.0f)
                                    .fillMaxWidth(0.5f)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gallery_unfilled),
                                    contentDescription = "Open Gallery",
                                    tint = MaterialTheme.colorScheme.inverseSurface,
                                    modifier = Modifier.fillMaxSize(0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(0.05f))

                        // Camera button
                        Box(
                            modifier = Modifier
                                .weight(0.4f),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = {
                                    if (hasCameraPermission) {
                                        // Create the file for the camera photo
                                        val photoFile = createImageFile()
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            photoFile
                                        )
                                        imageUri.value = uri
                                        cameraLauncher.launch(uri)
                                    } else {
                                        // Request camera permission
                                        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_camera_unfilled),
                                    contentDescription = "Open Camera",
                                    tint = MaterialTheme.colorScheme.inverseSurface,
                                    modifier = Modifier.fillMaxSize(0.6f)
                                )
                            }
                        }

                        // 30% empty space
                        Spacer(modifier = Modifier.weight(0.25f))
                    }
                }
            }
        }
    }
}

// Extension pour naviguer vers OrganChoice
fun androidx.navigation.NavHostController.navigateToOrganChoice(
    imageUri: Uri,
    latitude: Double?,
    longitude: Double?,
    hasValidLocation: Boolean
) {
    this.navigate("organ_choice?imageUri=${imageUri}&latitude=${latitude}&longitude=${longitude}&hasValidLocation=${hasValidLocation}")
}

// Function to get the location and update the text
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLocationAndUpdateText(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationUpdated: (String, Double?, Double?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        if (location != null) {
            // Convert the latitude and longitude into a city name
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (address?.isNotEmpty() == true) {
                onLocationUpdated(address[0].locality ?: "Unknown City", location.latitude, location.longitude)
            } else {
                onLocationUpdated("Unknown Location", location.latitude, location.longitude)
            }
        } else {
            onLocationUpdated("Location not available", null, null)
        }
    }
}