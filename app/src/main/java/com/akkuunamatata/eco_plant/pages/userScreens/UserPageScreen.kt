package com.akkuunamatata.eco_plant.pages.userScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
@Preview
@Composable
fun UserPageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre "Settings"
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grande icône
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 50.dp) // Effet circulaire avec fondu
                ) {
                    // TODO: change icon from db
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit_profile),
                contentDescription = null,
                modifier = Modifier.size(150.dp), // Taille de l'icône augmentée
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nom et email
        Text(
            text = "name",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "mail",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Boutons empilés verticalement avec séparateurs
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsButton(label = stringResource(R.string.username))
            Divider() // Ligne de séparation
            SettingsButton(label = stringResource(R.string.email))
            Divider()
            SettingsButton(label = stringResource(R.string.password))
            Divider()
            SettingsButton(label = stringResource(R.string.language))
            Divider()
            SettingsButton(label = stringResource(R.string.logout))
        }
    }
}

@Composable
fun SettingsButton(label: String, modifier: Modifier = Modifier) {
    TextButton( // Utilisation de TextButton pour un fond transparent
        onClick = { /* TODO: Handle click */ },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null
            )
        }
    }
}