package com.akkuunamatata.eco_plant.pages.userScreens.userSettingsScreens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LogoutSettingsScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    showDialog = true

    AlertDialog(
        onDismissRequest = { showDialog = false },
        title = { Text(text = stringResource(R.string.logout)) },
        text = { Text(text = stringResource(R.string.are_you_sure_to_logout)) },
        confirmButton = {
            TextButton(onClick = {
                showDialog = false
                // deconnexion de firebase
                FirebaseAuth.getInstance().signOut()
                navController.navigate("settings") {
                    popUpTo("settings") { inclusive = true }
                }
            }) {
                Text(text = stringResource(R.string.im_sure))
            }
        },
        dismissButton = {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )


}