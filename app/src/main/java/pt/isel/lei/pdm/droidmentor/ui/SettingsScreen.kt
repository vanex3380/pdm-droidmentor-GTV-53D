package pt.isel.lei.pdm.droidmentor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    vm: SettingsViewModel,
    onNavigate: (Screen) -> Unit,
) {
    val apiKey by vm.input.collectAsStateWithLifecycle()
    val hasKey by vm.hasKey.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f).padding(top = 24.dp)) {
                Button(onClick = { onNavigate(Screen.TITLE) }, shape = CircleShape) {
                    Text(text = "⬅️", textAlign = TextAlign.Center)
                }
            }
            Box(modifier = Modifier.weight(6f), contentAlignment = Alignment.Center) {
                Text(
                    text = "DroidMentor",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
            Box(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .padding(top = 216.dp)
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = apiKey,
                    onValueChange = vm::onInputChange,
                    label = { Text("Chave da API Gemini") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                Button(
                    onClick = vm::save,
                    modifier = Modifier.fillMaxHeight(),
                    shape = RectangleShape,
                ) {
                    Text("Guardar", fontSize = 15.sp, maxLines = 1)
                }
            }
        }

        // A chave em si não é mostrada (é um segredo)
        Text(
            text = if (hasKey) "Chave configurada ✅" else "Sem chave configurada",
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
