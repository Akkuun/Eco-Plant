package com.akkuunamatata.eco_plant.pages.plotScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotSettingsScreen(
    plotId: String,
    navController: NavHostController
) {
    var plotName by remember { mutableStateOf("") }
    var personalNotes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val loginRequiredMessage = stringResource(id = R.string.login_required)
    val errorLoadingMessage = stringResource(id = R.string.error_loading)
    val plotNotFoundMessage = stringResource(id = R.string.plot_not_found)
    val errorSavingMessage = stringResource(id = R.string.error_saving)
    val errorDeletingMessage = stringResource(id = R.string.error_deleting)

    // Charger les données existantes
    LaunchedEffect(plotId) {
        if (currentUser != null) {
            try {
                val plotDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("plots")
                    .document(plotId)
                    .get()
                    .await()

                if (plotDoc.exists()) {
                    plotName = plotDoc.getString("name") ?: ""
                    personalNotes = plotDoc.getString("notes") ?: ""
                } else {
                    errorMessage = plotNotFoundMessage
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "$errorLoadingMessage: ${e.message ?: ""}"
                isLoading = false
            }
        } else {
            errorMessage = loginRequiredMessage
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
            )
        } else {
            // Titre
            Text(
                text = stringResource(id = R.string.plot_settings),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Nom de la parcelle
            OutlinedTextField(
                value = plotName,
                onValueChange = { plotName = it },
                label = { Text(stringResource(id = R.string.plot_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                enabled = !isSaving
            )

            // Notes personnelles
            OutlinedTextField(
                value = personalNotes,
                onValueChange = { personalNotes = it },
                label = { Text(stringResource(id = R.string.personal_notes)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                enabled = !isSaving
            )

            // Bouton de suppression
            Button(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !isSaving
            ) {
                Text(stringResource(id = R.string.delete_plot))
            }

            // Afficher les messages d'erreur
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Boutons d'action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    enabled = !isSaving
                ) {
                    Text(stringResource(id = R.string.cancel))
                }

                Button(
                    onClick = {
                        isSaving = true
                        db.collection("users")
                            .document(currentUser?.uid ?: "")
                            .collection("plots")
                            .document(plotId)
                            .update(
                                mapOf(
                                    "name" to plotName,
                                    "notes" to personalNotes,
                                    "lastEdited" to com.google.firebase.Timestamp.now()
                                )
                            )
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                isSaving = false
                                errorMessage = "$errorSavingMessage: ${e.message ?: ""}"
                            }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    enabled = plotName.isNotBlank() && !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }

        // Boîte de dialogue de confirmation de suppression
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(id = R.string.confirm_deletion)) },
                text = { Text(stringResource(id = R.string.delete_plot_confirmation)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                            isSaving = true
                            db.collection("users")
                                .document(currentUser?.uid ?: "")
                                .collection("plots")
                                .document(plotId)
                                .delete()
                                .addOnSuccessListener {
                                    navController.navigate(Routes.PLOT_LIST) {
                                        popUpTo(Routes.PLOT_LIST) { inclusive = true }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isSaving = false
                                    errorMessage = "$errorDeletingMessage: ${e.message ?: ""}"
                                }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(stringResource(id = R.string.delete))
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showDeleteConfirmation = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}