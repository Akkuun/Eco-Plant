package com.akkuunamatata.eco_plant.pages.userScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.ui.theme.InterTypography
import com.google.firebase.auth.FirebaseAuth


/**
 * Displays the user change settings screen.
 *
 * @param navController The NavHostController for navigation.
 */
@Composable
fun UserChangeSettingsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Section supérieure : Informations utilisateur
        UserInfoSection()

        Spacer(modifier = Modifier.height(32.dp))

        // Section des boutons
        UserSettingsButtons(onSettingSelected = { selectedRoute ->
            when (selectedRoute) {
                "username" -> navController.navigate("settingsDetail/ChangeUsername")
                "email" -> navController.navigate("settingsDetail/ChangeEmail")
                "pwd" -> navController.navigate("settingsDetail/ChangePassword")
                "lang" -> navController.navigate("settingsDetail/lang")
                "logout" -> navController.navigate("settingsDetail/logout")
                "delete" -> navController.navigate("settingsDetail/delete")
                "switch" -> navController.navigate("settingsDetail/switch")
                else -> Unit
            }
        })
    }
}

/**
 * Displays the user information section.
 */
@Composable
fun UserInfoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // get the current user information from firebase
        val user = FirebaseAuth.getInstance().currentUser
        val name = user?.displayName ?: "Unknown"
        val email = user?.email ?: "Unknown"

        Text(
            text = stringResource(R.string.settings),
            style = InterTypography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grande icône
        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = false,
                        radius = 50.dp
                    )
                ) {
                    // TODO: change icon from db
                }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_edit_profile),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nom et email
        Text(
            text = name,
            style = InterTypography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = email,
            style = InterTypography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * Displays a horizontal divider of button used for the page.
 */
@Composable
fun UserSettingsButtons(onSettingSelected: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsButton(label = stringResource(R.string.username), onClick = { onSettingSelected("username") })
        HorizontalDivider()
//        SettingsButton(label = stringResource(R.string.email), onClick = { onSettingSelected("email") })
//        HorizontalDivider() TODO put bqck when working
        SettingsButton(label = stringResource(R.string.password), onClick = { onSettingSelected("pwd") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.language), onClick = { onSettingSelected("lang") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.logout), onClick = { onSettingSelected("logout") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.switch_mode), onClick = { onSettingSelected("switch") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.delete_account), onClick = { onSettingSelected("delete") })
        HorizontalDivider()
    }
}




@Composable
fun SettingsButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = InterTypography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


