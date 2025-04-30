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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest


/**
 * Composable function for the Sign In screen.
 *
 * @param NavigationController The navigation controller for handling navigation.
 */
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
                        if (!checkPassword(password, context)) {
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
                                        val profileUpdates = userProfileChangeRequest {
                                            displayName = name
                                        }
                                        user?.updateProfile(profileUpdates)
                                            ?.addOnCompleteListener { profileTask ->
                                                if (profileTask.isSuccessful) {
                                                    user.sendEmailVerification()
                                                        ?.addOnCompleteListener { emailTask ->
                                                            if (emailTask.isSuccessful) {
                                                                // Rediriger vers la page de vérification d'email
                                                                NavigationController.navigate("mailCheckup")
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
                                                        "Erreur lors de la mise à jour du profil : ${profileTask.exception?.message}",
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

/**
 * Check if the confirm password matches the original password.
 *
 * A valid confirm password must not be empty and must match the original password.
 */
fun checkConfirmPassword(password: String, confirmPassword: String): Boolean {
    if (confirmPassword.isEmpty()) {
        return false
    }
    if (password != confirmPassword) {
        return false
    }
    return true;

}

/**
 * Check if the password is valid.
 *
 * A valid password must not be empty, must be at least 6 characters long, with at least one digit,
 */
fun checkPassword(password: String, context: android.content.Context): Boolean {
    var isValid = true
    if (password.isEmpty()) {
        isValid = false
    }
    if (password.length < 6) {
        isValid = false
    }
    if (!password.any { it.isDigit() }) {
        isValid = false
    }
    if (!password.any { it.isLetter() }) {
        isValid = false
    }
    if (!password.any { it.isUpperCase() }) {
        isValid = false
    }
    if (!password.any { it.isLowerCase() }) {
        isValid = false
    }
    if (!isValid) {
        Toast.makeText(context, context.getString(R.string.error_password), Toast.LENGTH_SHORT)
            .show()
    }
    return isValid
}

/**
 * Check if the email is valid.
 *
 * A valid email must not be empty and must match the standard email pattern.
 */
fun checkEmail(email: String): Boolean {
    if (email.isEmpty()) {
        return false
    }
    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return false
    }
    return true;

}

/**
 * Check if the name is valid.
 *
 * A valid name must be at least 3 characters long and contain only letters and spaces.
 */
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

/**
 * Custom TextField Composable
 *
 * @param value The current text value of the TextField.
 * @param onValueChange Callback to be invoked when the text value changes.
 * @param label The label to display above the TextField.
 * @param isError Indicates if the TextField is in an error state.
 * @param modifier Modifier to be applied to the TextField.
 * @param isPassword Indicates if the TextField is for password input.
 * @param passwordVisible Indicates if the password is visible.
 * @param onPasswordToggle Callback to be invoked when the password visibility is toggled.
 * @param keyboardType The type of keyboard to be displayed.
 *
 */
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
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ) // Set the keyboard type
    )
}
