package com.akkuunamatata.eco_plant.pages.plantIdentificationScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akkuunamatata.eco_plant.R
import com.akkuunamatata.eco_plant.ui.theme.InterTypography

@Composable
fun ScanScreen(navController: androidx.navigation.NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Scan",

                )
                Text(
                    text = "Scan",
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                text = "GPS POSITION",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "SCAN",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {

                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.resend_email))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {

                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(id = R.string.back))
            }
        }
    }
}
