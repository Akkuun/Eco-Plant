package com.example.eco_plant.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun SignInScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        )
    }


}
