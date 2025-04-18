package com.example.eco_plant.pages


import android.widget.VideoView
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.eco_plant.R
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.lint.kotlin.metadata.Visibility



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {


// Partie haute (image arrondi)
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
        modifier = Modifier.fillMaxSize(),
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
           // Contenu de la boîte de connexion
           Text(
               text = stringResource(R.string.welcome),
               style = MaterialTheme.typography.displayLarge,
               color = MaterialTheme.colorScheme.onPrimaryContainer
           )
       }


        LoginForm();
    }
}
@Preview(showBackground = true)
@Composable
fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Partie basse (formulaire / connexion)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 15.dp), // Plus de padding à gauche
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Champ Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text(stringResource(R.string.email_adress)) },
                label = { Text(stringResource(R.string.email_adress)) },
                shape = RoundedCornerShape(16.dp), // arrondi
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Champ Password avec icône œil
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
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
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.forgot_password),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Handle forgot password */ }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Test Couleur",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            )
            Button(
                onClick = { /* Action */ },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = stringResource(R.string.not_a_member))
                Text(
                    text = " "+stringResource(R.string.register_now),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        // Action pour aller à la page d'inscription
                    }
                )
            }
        }
    }
}