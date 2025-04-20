package com.akkuunamatata.eco_plant.pages

import android.content.Context
import android.net.Uri
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
                organChosen(context, "Leaf", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.leaf),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.flower), onClick = {
                // Handle Button flower click
                organChosen(context, "Flower", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.flower),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.fruit), onClick = {
                // Handle Button fruit click
                organChosen(context, "Fruit", imageUri, latitude, longitude, hasValidLocation)
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
                organChosen(context, "Bark", imageUri, latitude, longitude, hasValidLocation)
            },
                icon = painterResource(id = R.drawable.bark),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.full_plant), onClick = {
                // Handle Button full plant click
                organChosen(context, "Full Plant", imageUri, latitude, longitude, hasValidLocation)
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
    Toast.makeText(
        context,
        "Organ chosen: $organ. To do : use PlantNet API",
        Toast.LENGTH_SHORT
    ).show()
}

fun navigateToPlantIdentification(
    navController: androidx.navigation.NavHostController,
) {
    navController.navigate("scan")
}