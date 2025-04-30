package com.akkuunamatata.eco_plant.pages.userScreens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

/**
 * Composable function for the email verification screen.
 * Guides the user to verify their email and handles email verification logic.
 *
 * @param navController Navigation controller for navigating between screens.
 */
@Composable
fun EmailVerificationScreen(navController: androidx.navigation.NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    // Periodically checks if the user's email is verified
    LaunchedEffect(Unit) {
        checkEmailVerification(auth, context, navController)
    }

    // UI layout for the email verification screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        EmailVerificationContent(auth, context, navController)
    }
}

/**
 * Periodically checks if the user's email is verified and updates Firestore if verified.
 */
private suspend fun checkEmailVerification(
    auth: FirebaseAuth,
    context: android.content.Context,
    navController: androidx.navigation.NavHostController
) {
    while (true) {
        val user = auth.currentUser
        user?.reload()?.addOnCompleteListener {
            if (user?.isEmailVerified == true) {
                saveUserDataToFirestore(user, context, navController)
            }
        }
        delay(3000) // Check every 5 seconds
    }
}

/**
 * Saves the user's data to Firestore after email verification.
 */
private fun saveUserDataToFirestore(
    user: com.google.firebase.auth.FirebaseUser,
    context: android.content.Context,
    navController: androidx.navigation.NavHostController
) {
    val uid = user.uid
    val userData = hashMapOf(
        "name" to user.displayName,
        "email" to user.email,
        "lang" to Locale.current.language // Get system language
    )

    FirebaseFirestore.getInstance().collection("users").document(uid)
        .set(userData)
        .addOnSuccessListener {
            Toast.makeText(context, "Account successfully activated!", Toast.LENGTH_SHORT).show()
            navController.navigate("settings") // Navigate to settings screen
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "Error saving data: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}

/**
 * Displays the content of the email verification screen.
 */
@Composable
private fun EmailVerificationContent(
    auth: FirebaseAuth,
    context: android.content.Context,
    navController: androidx.navigation.NavHostController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instruction text
        Text(
            text = "Please verify your email to activate your account.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Email sent notification
        Text(
            text = "A verification email has been sent to ${auth.currentUser?.email}.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Resend email button
        Button(
            onClick = { resendVerificationEmail(auth, context) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.resend_email))
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Back button
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("settings") // Navigate back to settings
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = stringResource(id = R.string.back))
        }
    }
}

/**
 * Resends the verification email to the user.
 */
private fun resendVerificationEmail(auth: FirebaseAuth, context: android.content.Context) {
    auth.currentUser?.sendEmailVerification()
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Verification email resent.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Error sending email: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}