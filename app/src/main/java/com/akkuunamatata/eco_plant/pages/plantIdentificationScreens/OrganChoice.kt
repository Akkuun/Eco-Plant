package com.akkuunamatata.eco_plant.pages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.akkuunamatata.eco_plant.ui.theme.InterTypography

@Composable
fun OrganChoice(
    navController: androidx.navigation.NavHostController,
    imageUri: Uri,
    latitude: Double?,
    longitude: Double?,
    hasValidLocation: Boolean
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Latitude: ${latitude ?: "?"}")
        Text("Longitude: ${longitude ?: "?"}")
        Text("Valid Location: $hasValidLocation")

        Spacer(modifier = Modifier.height(16.dp))
        imageUri.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            )
        }
    }
}
