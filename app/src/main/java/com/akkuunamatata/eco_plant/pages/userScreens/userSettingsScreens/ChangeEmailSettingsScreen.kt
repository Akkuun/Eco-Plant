package com.akkuunamatata.eco_plant.pages.userScreens.userSettingsScreens

import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChangeEmailSettingsScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoSection()
        Spacer(modifier = Modifier.height(32.dp))

        // Champ pour l'email
        CustomTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = stringResource(R.string.email_adress),
            isError = emailError,
            keyboardType = KeyboardType.Email
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
                    emailError =
                        email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                            .matches()
                    // log email
                    Toast.makeText(
                        navController.context,
                        "Email: $email",
                        Toast.LENGTH_SHORT
                    ).show()
                   val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.updateEmail(email)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                navController.context,
                                "Email updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                navController.context,
                                "Failed to update email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = stringResource(R.string.update_email))
            }
        }
    }
}