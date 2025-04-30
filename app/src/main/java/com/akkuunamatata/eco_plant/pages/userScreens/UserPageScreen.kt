package com.akkuunamatata.eco_plant.pages.userScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.ui.theme.InterTypography


@Composable
fun UserInfoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre "Settings"
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
            text = "name",
            style = InterTypography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "mail",
            style = InterTypography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun UserSettingsButtons(onSettingSelected: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsButton(label = stringResource(R.string.username), onClick = { onSettingSelected("basic") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.email), onClick = { onSettingSelected("basic") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.password), onClick = { onSettingSelected("pwd") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.language), onClick = { onSettingSelected("lang") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.logout), onClick = { onSettingSelected("logout") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.delete_account), onClick = { onSettingSelected("delete") })
        HorizontalDivider()
        SettingsButton(label = stringResource(R.string.switch_mode), onClick = { onSettingSelected("switch") })
    }
}

@Composable
fun ChangeSelectedUserSettings(selectedItem: String) {
    when (selectedItem) {
        "pwd" -> PasswordSettings()
        "basic" -> BasicSettings()
        "mode" -> ModeSettings()
        "lang" -> LanguageSettings()
        "logout" -> LogoutSettings()
        "delete" -> DeleteAccountSettings()
        "switch" -> SwitchModeSettings()
        else -> DefaultSettings(selectedItem)
    }
}
@Composable
fun PasswordSettings() {

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoSection();
        Spacer(modifier = Modifier.height(32.dp))
        // Champ pour le nouveau mot de passe
        CustomTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                newPasswordError = false
            },
            label = stringResource(R.string.new_password),
            isError = newPasswordError,
            isPassword = true,
            passwordVisible = newPasswordVisible,
            onPasswordToggle = { newPasswordVisible = !newPasswordVisible },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ pour confirmer le mot de passe
        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = false
            },
            label = stringResource(R.string.confirm_password),
            isError = confirmPasswordError,
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardType = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Boutons côte à côte
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* TODO: Handle back action */ },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.back))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    newPasswordError = newPassword.isEmpty()
                    confirmPasswordError = confirmPassword.isEmpty() || newPassword != confirmPassword

                    if (!newPasswordError && !confirmPasswordError) {
                        // TODO: Handle change password action
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.change_password))
            }
        }
    }
}

@Composable
fun BasicSettings() {
    // Contenu pour changer le nom ou l'email
    Text(text = "Changer le nom ou l'email")
}

@Composable
fun ModeSettings() {
    // Contenu pour changer le mode
    Text(text = "Changer le mode")
}

@Composable
fun LanguageSettings() {
    // Contenu pour changer la langue
    Text(text = "Changer la langue")
}

@Composable
fun LogoutSettings() {
    // Contenu pour se déconnecter
    Text(text = "Se déconnecter")
}

@Composable
fun DeleteAccountSettings() {
    // Contenu pour supprimer le compte
    Text(text = "Supprimer le compte")
}

@Composable
fun SwitchModeSettings() {
    // Contenu pour changer de mode
    Text(text = "Changer de mode")
}

@Composable
fun DefaultSettings(selectedItem: String) {
    // Section inférieure : Boutons de paramètres
    UserSettingsButtons (onSettingSelected = { /* Do nothing */ })
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
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
fun UserPageScreen(navController: androidx.navigation.NavHostController) {
    var selectedSettingsToChange by remember { mutableStateOf("") }

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
        UserSettingsButtons(onSettingSelected = { selectedSettingsToChange ->
            navController.navigate("settingsDetail/$selectedSettingsToChange")
        })

        Spacer(modifier = Modifier.height(32.dp))

        // Section dynamique : Contenu basé sur la sélection
        ChangeSelectedUserSettings(selectedItem = selectedSettingsToChange)
    }
}


@Composable
fun SettingsDetailScreen(selectedSetting: String, navController: androidx.navigation.NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Spacer(modifier = Modifier.height(16.dp))

        // Affichage du contenu spécifique
        ChangeSelectedUserSettings(selectedItem = selectedSetting)
    }
}
@Preview
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "settings") {
        composable("settings") {
            UserPageScreen(navController = navController)
        }
        composable("settingsDetail/{selectedSetting}") { backStackEntry ->
            val selectedSetting = backStackEntry.arguments?.getString("selectedSetting") ?: ""
            SettingsDetailScreen(selectedSetting = selectedSetting, navController = navController)
        }
    }
}