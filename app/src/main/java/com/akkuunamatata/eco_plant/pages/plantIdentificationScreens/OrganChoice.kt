package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.akkuunamatata.eco_plant.R
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.akkuunamatata.eco_plant.utils.PlantNetKey
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

@Composable
fun ButtonWithImage(
    label: String,
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                onClick = onClick,
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // Image as the button background
        Image(
            painter = icon,
            contentDescription = label,
            modifier = Modifier.fillMaxSize()
                .blur(1.dp),
            contentScale = ContentScale.Crop
        )

        // Text positioned in the center
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 1f),
                        offset = Offset(4f, 4f),
                        blurRadius = 8.0f
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,

            )
        }
    }
}

@Composable
fun OrganChoice(
    navController: androidx.navigation.NavHostController,
    imageUri: Uri,
    latitude: Double?,
    longitude: Double?,
    hasValidLocation: Boolean
) {
    val context = LocalContext.current
    val screenHeight = context.resources.displayMetrics.heightPixels
    val maxHeight = (screenHeight * 0.15).dp // 40% of the screen height

    Column(modifier = Modifier.fillMaxSize().padding(0.dp)) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.height(maxHeight)
                .graphicsLayer {
                    shape = RoundedCornerShape(12.dp)
                    clip = true
                }
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Title
        Text(
            text = stringResource(id = R.string.organ_choice_title),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ButtonWithImage(stringResource(R.string.leaf), onClick = {
                // Handle Button leaf click
                organChosen(context, "leaf", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.leaf),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.flower), onClick = {
                // Handle Button flower click
                organChosen(context, "flower", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.flower),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.fruit), onClick = {
                // Handle Button fruit click
                organChosen(context, "fruit", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.fruit),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ButtonWithImage(stringResource(R.string.bark), onClick = {
                // Handle Button bark click
                organChosen(context, "bark", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.bark),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.full_plant), onClick = {
                // Handle Button full plant click
                organChosen(context, "auto", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.plant),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.other), onClick = {
                // Handle Button other click
                organChosen(context, "Other", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.plant_other),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Le beau temps
        Button(
            onClick = {
                navigateToPlantIdentification(
                    navController
                )
            },
            shape = RoundedCornerShape(36.dp),
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

fun organChosen(
    context: Context,
    organ: String,
    imageUri: Uri,
    latitude: Double?,
    longitude: Double?,
    hasValidLocation: Boolean
) {
    val key = PlantNetKey.getApiKey()
    val client = OkHttpClient()

    try {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val tempFile = File(context.cacheDir, "plant_image.jpg")

        tempFile.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        println("Taille du fichier: ${tempFile.length()} octets")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "images",
                "plant_image.jpg",
                tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .addFormDataPart("organs", organ)
            .build()

        val request = Request.Builder()
            .url("https://my-api.plantnet.org/v2/identify/all?api-key=$key")
            .post(requestBody)
            .build()

        println("Envoi de la requête à PlantNet...")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                (context as? android.app.Activity)?.runOnUiThread {
                    Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                tempFile.delete()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body?.string()
                    if (response.isSuccessful && responseData != null) {
                        val jsonResponse = JSONObject(responseData)
                        val resultsArray = jsonResponse.getJSONArray("results")

                        if (resultsArray.length() == 0) {
                            println("Aucun résultat trouvé")
                            (context as? android.app.Activity)?.runOnUiThread {
                                Toast.makeText(context, "Aucun résultat trouvé", Toast.LENGTH_LONG).show()
                            }
                            return
                        }

                        val topResult = resultsArray.getJSONObject(0)

                        val species = topResult.getJSONObject("species")
                        val scientificName = species.optString("scientificNameWithoutAuthor", "Inconnu")
                        val score = topResult.optDouble("score", 0.0)

                        println("Meilleure correspondance: $scientificName (score: ${(score * 100).toInt()}%)")

                        (context as? android.app.Activity)?.runOnUiThread {
                            Toast.makeText(context, "Plante identifiée: $scientificName", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        println("Erreur: Code ${response.code} - Corps: $responseData")
                        (context as? android.app.Activity)?.runOnUiThread {
                            if (response.code == 404) {
                                Toast.makeText(
                                    context,
                                    "Aucune plante détectée. Veuillez prendre une meilleure photo.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(context, "Échec API: ${response.code}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    tempFile.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Erreur lors de la lecture de la réponse: ${e.message}")
                }

            }
        })
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Erreur de traitement d'image: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun navigateToPlantIdentification(
    navController: androidx.navigation.NavHostController,
) {
    navController.navigate("scan")
}