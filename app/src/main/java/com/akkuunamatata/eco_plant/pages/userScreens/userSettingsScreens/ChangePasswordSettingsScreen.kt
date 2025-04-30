package com.akkuunamatata.eco_plant.pages.userScreens.userSettingsScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.pages.userScreens.CustomTextField
import com.akkuunamatata.eco_plant.pages.userScreens.UserInfoSection

@Composable
fun ChangePasswordSettingsScreen(navController: NavHostController) {

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
                onClick = { navController.popBackStack() },
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