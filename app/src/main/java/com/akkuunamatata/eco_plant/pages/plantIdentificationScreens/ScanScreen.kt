package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import ScanNotLoggedInScreen
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.components.LocationBar
import com.akkuunamatata.eco_plant.utils.getLocationAndUpdateText
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScanScreen(
    navController: NavHostController,
    // get if the user is logged in
    isUserLoggedIn: Boolean = FirebaseAuth.getInstance().currentUser != null
) {
    // if not login, show the ScanNotLoggedInScreen.kt
    if (!isUserLoggedIn) {
        ScanNotLoggedInScreen(navController)
        return
    }
    //else show the Scan page
    Scan(navController)
}

@Composable
fun Scan(navController: NavHostController) {
    var locationText by remember { mutableStateOf("Position") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasGalleryPermission by remember { mutableStateOf(false) }
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

    // Fonction pour copier un URI de la galerie vers un fichier local
    // Cette fonction est essentielle pour gérer les URI externes de la galerie
    fun copyUriToLocalFile(uri: Uri): Uri? {
        try {
            // Créer un fichier temporaire pour stocker l'image
            val destinationFile = createImageFile()

            // Ouvrir l'URI source et copier son contenu dans le fichier de destination
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Créer un URI FileProvider à partir du fichier local
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                destinationFile
            )
        } catch (e: Exception) {
            Log.e("Scan", "Error copying URI to local file", e)
            return null
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

    // Lancer la galerie
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                Log.d("Scan", "Gallery image selected: $it")

                // Copier l'URI de la galerie vers un fichier local avec un URI FileProvider
                val localUri = copyUriToLocalFile(it)

                // Si la copie a réussi, naviguer avec le nouvel URI
                localUri?.let { newUri ->
                    Log.d("Scan", "Copied to local URI: $newUri")
                    navController.navigateToOrganChoice(newUri)
                } ?: run {
                    // Si la copie a échoué, essayer de naviguer avec l'URI original
                    Log.d("Scan", "Copying failed, trying with original URI")
                    navController.navigateToOrganChoice(it)
                }
            } catch (e: Exception) {
                Log.e("Scan", "Error processing gallery image", e)
            }
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            // After image is captured, load the bitmap from the URI
            val uri = imageUri.value
            if (uri != null) {
                Log.d("Scan", "Camera image captured: $uri")
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    bitmapImage = bitmap

                    val savedUri = saveBitmapToEcoPlantFolder(context, bitmap)

                    if (savedUri != null) {
                        Log.d("Scan", "Saved camera image: $savedUri")
                        navController.navigateToOrganChoice(savedUri)
                    } else {
                        Log.d("Scan", "Using original camera URI: $uri")
                        navController.navigateToOrganChoice(uri)
                    }
                } catch (e: Exception) {
                    Log.e("Scan", "Error processing camera image", e)
                }
            }
        }
    }

    // Gestion des permissions de localisation
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
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

    // Gestion des permissions de caméra
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasCameraPermission = granted
        if (granted) {
            // Permission accordée, on peut lancer la caméra
            val photoFile = createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            imageUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            Log.d("ScanScreen", "Camera permission denied")
        }
    }

    // Gestion des permissions d'accès à la galerie
    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.all { it.value }
        hasGalleryPermission = allGranted

        if (allGranted) {
            // Lancer la galerie une fois les permissions accordées
            galleryLauncher.launch("image/*")
        } else {
            Log.d("ScanScreen", "Gallery permissions denied")
        }
    }

    // Check permissions on first launch
    LaunchedEffect(Unit) {
        // Vérifier les permissions de localisation
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                hasLocationPermission = true
                getLocationAndUpdateText(fusedLocationClient, context) { location, lat, lon ->
                    locationText = location
                    latitude = lat
                    longitude = lon
                }
            }
            else -> {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Vérifier les permissions de caméra
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        // Vérifier les permissions de galerie
        hasGalleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
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
            LocationBar(
                onLocationUpdated = { location, lat, lon ->
                    locationText = location
                    latitude = lat
                    longitude = lon
                },
                modifier = Modifier.padding(16.dp)
            )

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
                                onClick = {
                                    // Vérifier et demander les permissions avant d'ouvrir la galerie
                                    if (hasGalleryPermission) {
                                        galleryLauncher.launch("image/*")
                                    } else {
                                        // Demander les permissions appropriées selon la version d'Android
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            galleryPermissionLauncher.launch(arrayOf(
                                                Manifest.permission.READ_MEDIA_IMAGES
                                            ))
                                        } else {
                                            galleryPermissionLauncher.launch(arrayOf(
                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                            ))
                                        }
                                    }
                                },
                                shape = CircleShape,
                                contentPadding = PaddingValues(5.dp),
                                modifier = Modifier
                                    .aspectRatio(1.0f)
                                    .fillMaxWidth(0.5f)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gallery_unfilled),
                                    contentDescription = "Open Gallery",
                                    tint = MaterialTheme.colorScheme.secondary,
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
                                    tint = MaterialTheme.colorScheme.secondary,
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
fun NavHostController.navigateToOrganChoice(
    imageUri: Uri,
) {
    Log.d("Navigation", "Navigating to organ_choice with URI: $imageUri")
    // Encoder l'URI pour éviter les problèmes de caractères spéciaux
    val encodedUri = Uri.encode(imageUri.toString())
    this.navigate("organ_choice?imageUri=$encodedUri")
}