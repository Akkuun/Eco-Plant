package com.akkuunamatata.eco_plant.pages

import android.net.Uri
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
            .clip(RoundedCornerShape(12.dp))
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
                        color = Color.Black.copy(alpha = 0.9f),
                        offset = Offset(3f, 3f),
                        blurRadius = 50.0f
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
    val maxHeight = (screenHeight * 0.8).dp // 40% of the screen height

    Column(modifier = Modifier.fillMaxSize().padding(0.dp)) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.0f).height(maxHeight)
                .graphicsLayer {
                    shape = RoundedCornerShape(12.dp)
                    clip = true
                }
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ButtonWithImage(stringResource(R.string.leaf), onClick = { /* Handle Button leaf click */ },
                icon = painterResource(id = R.drawable.leaf),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.flower), onClick = { /* Handle Button flower click */ },
                icon = painterResource(id = R.drawable.flower),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.fruit), onClick = { /* Handle Button fruit click */ },
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
            ButtonWithImage(stringResource(R.string.bark), onClick = { /* Handle Button bark click */ },
                icon = painterResource(id = R.drawable.bark),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.full_plant), onClick = { /* Handle Button full_plant click */ },
                icon = painterResource(id = R.drawable.plant),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )

            ButtonWithImage(stringResource(R.string.other), onClick = { /* Handle Button other click */ },
                icon = painterResource(id = R.drawable.plant_other),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .aspectRatio(1.0f)
            )
        }

        // Le beau temps
        Button (
            onClick = {
                // Handle Button retry click
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .padding(8.dp)
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}

