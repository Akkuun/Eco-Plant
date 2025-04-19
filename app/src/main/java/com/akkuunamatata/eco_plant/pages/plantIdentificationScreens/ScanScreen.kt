package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.ui.theme.InterTypography

@Composable
fun ScanScreen(navController: androidx.navigation.NavHostController) {
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
                    text = "Position",
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
                        // Handle refresh click
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
                                onClick = { /* Handle gallery click */ },
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
                                onClick = { /* Handle camera click */ },
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
