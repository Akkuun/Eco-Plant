package com.akkuunamatata.eco_plant.pages.userScreens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun EmailVerificationScreen(navController: androidx.navigation.NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            val user = auth.currentUser
            user?.reload()?.addOnCompleteListener {
                if (user?.isEmailVerified == true) {
                    // Enregistrer les données utilisateur dans Firestore
                    val uid = user.uid
                    val userData = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email
                    )
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Compte activé avec succès !",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("settings") // Rediriger vers l'écran de connexion
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Erreur lors de l'enregistrement des données : ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            kotlinx.coroutines.delay(5000) // Vérifie toutes les 5 secondes
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Veuillez vérifier votre email pour activer votre compte.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Un email de vérification a été envoyé à ${auth.currentUser?.email}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Email de vérification renvoyé.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de l'envoi de l'email : ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.resend_email))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("settings") // Rediriger vers l'écran de connexion
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.back))
            }
        }
    }
}