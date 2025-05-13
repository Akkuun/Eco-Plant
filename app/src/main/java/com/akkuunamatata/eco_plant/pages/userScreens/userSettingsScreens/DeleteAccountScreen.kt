package com.akkuunamatata.eco_plant.pages.userScreens.userSettingsScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
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
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DeleteAccountSettingsScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
       AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(R.string.confirm_delete_title)) },
            text = { Text(text = stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                       showDialog = false
//                       val user = FirebaseAuth.getInstance().currentUser
//                       val firestore = FirebaseFirestore.getInstance()
//                        // Delete user from Firestore Auth
                    }
                ) {
                    Text(text = stringResource(R.string.confirm_delete_title))
                }
            },
            dismissButton = {
                androidx.compose.material3.Button(
                    onClick = { showDialog = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }




    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInfoSection()

        Spacer(modifier = Modifier.height(16.dp))

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
                onClick = { showDialog = true },modifier = Modifier.weight(1f)

            ) {
                Text(text = stringResource(R.string.delete_account))
            }
        }
    }
}