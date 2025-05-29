import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.akkuunamatata.eco_plant.R

/**
 * Écran affiché lorsque l'utilisateur n'est pas connecté
 * Style inspiré de Google
 */
@Composable
fun PlotNotLoggedInScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo ou icône
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "List Icon",
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Titre de style Google
            Text(
                text = stringResource(R.string.users_plot),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Message explicatif
            Text(
                text = stringResource(R.string.eco_plant_feature_for_logged),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Bouton de connexion style Google
            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.Go_to_login),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 20.sp
                )
            }

            // Espace entre les boutons
            Spacer(modifier = Modifier.height(12.dp))

            // Bouton secondaire pour s'inscrire
            OutlinedButton(
                onClick = { navController.navigate("sign_in") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),  // Hauteur augmentée pour accommoder deux lignes
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)  // Padding interne
            ) {
                Text(
                    text = stringResource(R.string.create_an_account),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Visible,
                    lineHeight = 20.sp
                )
            }
        }
    }
}