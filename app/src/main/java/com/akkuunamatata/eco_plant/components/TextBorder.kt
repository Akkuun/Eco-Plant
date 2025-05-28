package com.akkuunamatata.eco_plant.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TextBorder( // Composable pour afficher du texte avec une "bordure"
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    textColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Float = 1.5f
) {
    Box(modifier = modifier) {
        val offset = borderWidth

        // Positions en diagonale
        for (xOffset in listOf(-offset, offset)) {
            for (yOffset in listOf(-offset, offset)) {
                Text(
                    text = text,
                    style = style,
                    color = borderColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = xOffset.dp, y = yOffset.dp),
                    textAlign = textAlign
                )
            }
        }

        // Positions horizontales et verticales
        listOf(
            Offset(0f, -offset),
            Offset(0f, offset),
            Offset(-offset, 0f),
            Offset(offset, 0f)
        ).forEach { (xOffset, yOffset) ->
            Text(
                text = text,
                style = style,
                color = borderColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = xOffset.dp, y = yOffset.dp),
                textAlign = textAlign
            )
        }

        // Texte principal au-dessus
        Text(
            text = text,
            style = style,
            color = textColor,
            modifier = Modifier.align(Alignment.Center),
            textAlign = textAlign
        )
    }
}