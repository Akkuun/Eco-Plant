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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.pages.userScreens.CustomTextField
import com.akkuunamatata.eco_plant.pages.userScreens.UserInfoSection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest

@Composable
fun ChangeUsernameSettingsScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoSection()
        Spacer(modifier = Modifier.height(32.dp))

        // Champ pour le nom d'utilisateur
        CustomTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = false
            },
            label = stringResource(R.string.username),
            isError = usernameError
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
                    usernameError = username.isEmpty() || username.length < 3

                   if (!usernameError) {
                       val user = FirebaseAuth.getInstance().currentUser
                       user?.updateProfile(userProfileChangeRequest {
                           displayName = username
                       })?.addOnCompleteListener { task ->
                           if (task.isSuccessful) {
                               navController.popBackStack()
                           } else {
                               usernameError = true
                           }
                       }
                   }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.update_username))
            }
        }
    }
}