package com.akkuunamatata.eco_plant.pages.userScreens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun SignInScreen(NavigationController: androidx.navigation.NavHostController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    val dl = FirebaseFirestore.getInstance()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                keyboardController?.hide()
            }
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.sign_in),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.create_an_account),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                // Name field
                CustomTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = stringResource(id = R.string.name),
                    isError = nameError
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Email field
                CustomTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = stringResource(id = R.string.email_adress),
                    isError = emailError,
                    keyboardType = KeyboardType.Email // Set the keyboard type to Email
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Password field
                CustomTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = stringResource(id = R.string.password),
                    isError = passwordError,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    keyboardType = KeyboardType.Password // Set the keyboard type to Password
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password field
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = false
                    },
                    label = stringResource(id = R.string.confirm_password),
                    isError = confirmPasswordError,
                    isPassword = true,
                    passwordVisible = confirmPasswordVisible,
                    onPasswordToggle = { confirmPasswordVisible = !confirmPasswordVisible },
                    keyboardType = KeyboardType.Password // Set the keyboard type to Password
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Sign Up button
                Button(
                    onClick = {
                        var isValid = true

                        if (!checkName(name)) {
                            nameError = true
                            isValid = false
                        }
                        if (!checkEmail(email)) {
                            emailError = true
                            isValid = false
                        }
                        if (!checkPassword(password)) {
                            passwordError = true
                            isValid = false
                        }
                        if (!checkConfirmPassword(password, confirmPassword)) {
                            confirmPasswordError = true
                            isValid = false
                        }

                        if (isValid) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        user?.sendEmailVerification()
                                            ?.addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    val uid = user.uid
                                                    if (uid != null) {
                                                        // Enregistrer le nom dans la base de données
                                                        val userData = hashMapOf(
                                                            "name" to name,
                                                            "email" to email
                                                        )
                                                        dl.collection("users").document(uid)
                                                            .set(userData)
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Inscription réussie. Veuillez vérifier votre email pour confirmer votre compte.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                                NavigationController.navigate("login") // Rediriger vers l'écran de connexion
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Toast.makeText(
                                                                    context,
                                                                    "Erreur lors de l'enregistrement des données : ${e.message}",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Erreur lors de l'envoi de l'email de confirmation : ${emailTask.exception?.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Inscription échouée : ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.sign_in))
                }
            }
        }
    }
}

fun checkConfirmPassword(password: String, confirmPassword: String): Boolean {
    if (confirmPassword.isEmpty()) {
        return false
    }
    if (password != confirmPassword) {
        return false
    }
    return true;

}

fun checkPassword(password: String): Boolean {
    if (password.isEmpty()) {
        return false
    }
    if (password.length < 6) {
        return false
    }
    if (!password.any { it.isDigit() }) {
        return false
    }
    if (!password.any { it.isLetter() }) {
        return false
    }
    if (!password.any { it.isUpperCase() }) {
        return false
    }
    if (!password.any { it.isLowerCase() }) {
        return false
    }
    return true;

}

fun checkEmail(email: String): Boolean {
    if (email.isEmpty()) {
        return false
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }
    return true;

}

// Function to check if the name is valid
fun checkName(name: String): Boolean {
    if (name.isEmpty()) {
        return false
    }
    if (name.length < 3) {
        return false
    }
    if (!name.all { it.isLetter() || it.isWhitespace() }) {
        return false
    }
    return true;
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                val image =
                    if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = null
                    )
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType) // Set the keyboard type
    )
}
