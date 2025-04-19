package com.akkuunamatata.eco_plant.pages.userScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.google.firebase.auth.FirebaseAuth


@Composable
fun SignInScreen(NavigationController: androidx.navigation.NavHostController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    var isChecked by remember { mutableStateOf(false) }
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
                // Champ Nom
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text(stringResource(id = R.string.name)) },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Champ Email
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = { Text(stringResource(id = R.string.email_adress)) },
                    isError = emailError,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Champ Mot de passe
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    label = { Text(stringResource(id = R.string.password)) },
                    isError = passwordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Champ Confirmation Mot de passe
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = false
                    },
                    label = { Text(stringResource(id = R.string.confirm_password)) },
                    isError = confirmPasswordError,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = image),
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Bouton Inscription
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
                                        Toast.makeText(
                                            context,
                                            "Inscription réussie",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        NavigationController.navigate("scan")
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Inscription échouée : ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Veuillez corriger les erreurs", Toast.LENGTH_SHORT).show()
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
    if(confirmPassword.isEmpty()){
        return false
    }
    if(password != confirmPassword){
        return false
    }
    return true;

}

fun checkPassword(password: String): Boolean {
    if(password.isEmpty()){
        return false
    }
    if(password.length < 6){
        return false
    }
    if(!password.any { it.isDigit() }){
        return false
    }
    if(!password.any { it.isLetter() }){
        return false
    }
    if(!password.any { it.isUpperCase() }){
        return false
    }
    if(!password.any { it.isLowerCase() }){
        return false
    }
    return true;

}

fun checkEmail(email: String): Boolean {
    if(email.isEmpty()){
        return false
    }
    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return false
    }
    return true;

}

// Function to check if the name is valid
fun checkName(name: String): Boolean {
    if(name.isEmpty()){
        return false
    }
    if(name.length < 3){
        return false
    }
    if(!name.all { it.isLetter() || it.isWhitespace() }){
        return false
    }
    return true;
}
