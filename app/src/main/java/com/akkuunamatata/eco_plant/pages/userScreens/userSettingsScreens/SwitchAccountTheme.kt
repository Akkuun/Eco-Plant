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
import androidx.compose.material3.RadioButton
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
import com.akkuunamatata.eco_plant.pages.userScreens.UserInfoSection
import com.akkuunamatata.eco_plant.ui.theme.InterTypography
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@Composable
fun SwitchAccountTheme(navController: NavHostController) {

    var selectedTheme by remember { mutableStateOf("dark") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoSection()
        Spacer(modifier = Modifier.height(32.dp))

        // Radio buttons for language selection
        // Radio buttons for theme selection
        Text(text = stringResource(R.string.change_theme), style = InterTypography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RadioButton(
                selected = selectedTheme == "dark",
                onClick = { selectedTheme = "dark" }
            )
            Text(text = stringResource(R.string.dark_mode), style = InterTypography.labelLarge)

            RadioButton(
                selected = selectedTheme == "light",
                onClick = { selectedTheme = "light" }
            )
            Text(text = stringResource(R.string.light_mode), style = InterTypography.labelLarge)
        }
        Spacer(modifier = Modifier.height(16.dp)) // Ajustement de l'espacement
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val db = FirebaseFirestore.getInstance()

                    userId?.let {
                        db.collection("users").document(it)
                            .update("theme", selectedTheme)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.popBackStack()
                                } else {
                                    // Gérer l'erreur
                                }
                            }
                    }
                },
                modifier = Modifier.weight(1f) // Utilisation de weight pour équilibrer les boutons
            ) {
                Text(text = stringResource(R.string.update_theme))
            }
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f) // Utilisation de weight pour équilibrer les boutons
            ) {
                Text(text = stringResource(R.string.back))
            }
        }

    }


}
