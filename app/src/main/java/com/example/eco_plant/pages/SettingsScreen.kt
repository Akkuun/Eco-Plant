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


        // Partie basse (formulaire / connexion)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(15.dp)
                .align(Alignment.Start)
            ,
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(50.dp))
                OutlinedTextField(placeholder = { Text(stringResource(R.string.email_adress)) }, value = "", onValueChange = {}, label = { Text(stringResource(R.string.email_adress)) })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    placeholder = { Text(stringResource(R.string.password)) },
                    value = "",
                    onValueChange = {},
                    label = { Text(stringResource(R.string.password)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.forgot_password),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(onClick = { /* Action */ }) {
                    Text(stringResource(R.string.login))
                }

            }
        }
    }
}