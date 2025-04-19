package com.akkuunamatata.eco_plant.pages.userScreens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.reflect.Modifier
@Composable
fun EmailVerificationScreen(navController: androidx.navigation.NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // Vérification périodique
    LaunchedEffect(Unit) {
        while (true) {
            val user = auth.currentUser
            user?.reload()?.addOnCompleteListener {
                if (user?.isEmailVerified == true) {
                    // Rediriger vers l'écran de connexion après vérification
                    Toast.makeText(context, "Compte activé avec succès !", Toast.LENGTH_SHORT).show()
                    navController.navigate("settings") {
                        popUpTo("mailCheckup") { inclusive = true }
                    }
                }
            }
            kotlinx.coroutines.delay(5000) // Vérifie toutes les 5 secondes
        }
    }

    // UI de la page
   Box(
       contentAlignment = Alignment.Center,
       modifier = androidx.compose.ui.Modifier.padding(start = 16.dp, top = 16.dp)
   ) {
       Column(
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Text(
               text = "Veuillez vérifier votre e-mail pour activer votre compte.",
               style = MaterialTheme.typography.bodyMedium,
               color = MaterialTheme.colorScheme.onBackground
           )
           androidx.compose.material3.Button(
               onClick = { navController.popBackStack() },
               modifier = androidx.compose.ui.Modifier.padding(top = 16.dp)
           ) {
               Text(text = "Retour")
           }
       }
   }
}