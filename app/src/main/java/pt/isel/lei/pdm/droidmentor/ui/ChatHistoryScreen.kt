package pt.isel.lei.pdm.droidmentor.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.lei.pdm.droidmentor.domain.Conversation

@Composable
fun ChatHistoryScreen(
    onNavigate: (Screen) -> Unit,
    conversations: List<Conversation>,
    onOpen: (Conversation) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Barra de topo: voltar + título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f).padding(top=24.dp)) {
                Button(onClick = { onNavigate(Screen.TITLE) }, shape = CircleShape) {
                    Text(text = "⬅\uFE0F",
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(
                modifier = Modifier.weight(6f).padding(top=24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Histórico", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(1f))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            for (conversa in conversations) {
                Button(
                    onClick = { onOpen(conversa) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                ) {
                    Text(text = conversa.title)
                }
            }
        }
    }
}