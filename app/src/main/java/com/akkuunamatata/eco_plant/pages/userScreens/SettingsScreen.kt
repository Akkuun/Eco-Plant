package com.akkuunamatata.eco_plant.pages.userScreens
import android.widget.Toast
import com.akkuunamatata.eco_plant.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun SettingsScreen(NavigationController: androidx.navigation.NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current // to hide the keyboard
    val interactionSource = remember { MutableInteractionSource() } // to handle clicks
    val auth = FirebaseAuth.getInstance()
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        // Upper part of the Form : background image
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 65.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clickable(interactionSource  = interactionSource, indication = null) { keyboardController?.hide() }, // On hide le clavier quand on touche l'image
                contentScale = ContentScale.Crop
            )
        }
        //Connexion box
        Box(
            modifier = Modifier
                .weight(0.4f)
                .padding(20.dp)
                .align(Alignment.Start), // Align the box to the left
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = stringResource(R.string.welcome),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable(interactionSource  = interactionSource, indication = null) { keyboardController?.hide() } // On hide le clavier quand on touche le texte
            )
        }

        // Bottom part of the Form
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 15.dp).clickable(interactionSource = interactionSource, indication = null) { keyboardController?.hide() }, // Plus de padding à gauche
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth().clickable(interactionSource  = interactionSource, indication = null) { keyboardController?.hide() }, // Plus de padding à gauche
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                // Email text field
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false // Réinitialise l'erreur lorsque l'utilisateur modifie le champ
                    },
                    placeholder = { Text(stringResource(R.string.email_adress)) },
                    label = { Text(stringResource(R.string.email_adress)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError // Affiche une bordure rouge si emailError est vrai
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password text field
                OutlinedTextField(
                    //type password
                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next ), // type password and to to next field and hide if clicked away
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false // Réinitialise l'erreur lorsque l'utilisateur modifie le champ
                    },
                    placeholder = { Text(stringResource(R.string.password)) },
                    label = { Text(stringResource(R.string.password)) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
                            )
                        }
                    },
                    isError = passwordError // Affiche une bordure rouge si passwordError est vrai
                )

                Spacer(modifier = Modifier.height(16.dp))
                // Forgot password button
                Text(
                    text = stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { /* Handle forgot password */ }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        emailError = email.isEmpty()
                        passwordError = password.isEmpty()

                        if (!emailError && !passwordError) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        val userEmail = user?.email

                                        if (userEmail != null) {
                                            // Vérifier si l'email existe dans Firestore
                                            val db = FirebaseFirestore.getInstance()
                                            db.collection("users")
                                                .whereEqualTo("email", userEmail)
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    if (documents.isEmpty) {
                                                        // Email non trouvé dans Firestore
                                                        Toast.makeText(
                                                            context,
                                                            "Compte non activé. Veuillez vérifier votre email.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        // Naviguer si l'email est trouvé
                                                        NavigationController.navigate("scan")
                                                    }
                                                }

                                        }
                                    } else {
                                        emailError = true
                                        passwordError = true
                                        showSnackbar = true // Affiche le Snackbar
                                    }
                                }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                // Snackbar for login error
                if (showSnackbar) {
                    Snackbar(
                        action = {
                            Text(
                                text = stringResource(R.string.close),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { showSnackbar = false } // Ferme le Snackbar
                            )
                        },
                        content = {
                            Text(text = stringResource(R.string.login_failed))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.not_a_member),
                        fontSize = MaterialTheme.typography.displaySmall.fontSize
                    )
                    Text(
                        text = " " + stringResource(R.string.register_now),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = MaterialTheme.typography.displaySmall.fontSize,
                        modifier = Modifier.clickable {
                            // Handle register now click
                            NavigationController.navigate("sign_in")


                        }
                    )
                }
            }

        }
    }
}


