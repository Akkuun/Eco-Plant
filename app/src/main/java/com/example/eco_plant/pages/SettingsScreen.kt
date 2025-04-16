package com.example.eco_plant.pages


import android.widget.VideoView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.eco_plant.R


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {

       // Partie haute (video)
       Box(
           modifier = Modifier
               .weight(1f) // prend 30% de la hauteur
               .fillMaxWidth(),
           contentAlignment = Alignment.Center
       ) {
           AndroidView(
               factory = { context ->
                   VideoView(context).apply {
                       setVideoPath("android.resource://" + context.packageName + "/" + R.raw.animationlogin)
                       setOnPreparedListener { it.isLooping = true }
                       start()
                   }
               },
               modifier = Modifier.fillMaxWidth()
           )
       }

        // Partie basse (formulaire / connexion)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Connexion", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Email") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Mot de passe") })
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { /* Action */ }) {
                    Text("Se connecter")
                }
            }
        }
    }
}