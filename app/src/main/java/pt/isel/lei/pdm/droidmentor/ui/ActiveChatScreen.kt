package pt.isel.lei.pdm.droidmentor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Role

@Composable
fun ActiveChatScreen(
    onNavigate: (Screen) -> Unit,
    conversation: Conversation?,
    onSend: (String) -> Unit,
) {
    var input by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { onNavigate(Screen.CHAT_HISTORY) }, modifier = Modifier.width(50.dp).padding(top=24.dp), shape = CircleShape) {
                Text(text = "⬅\uFE0F",
                    textAlign = TextAlign.Center)
            }
            Text(
                text = conversation?.title ?: "Nova conversa",
                modifier = Modifier.padding(top=24.dp, start = 12.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,

            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            conversation?.messages?.forEach { msg ->
                val isUser = msg.role == Role.USER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                ) {
                    Text(
                        text = msg.text,
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = if (isUser) Color(0xFF2962FF) else Color(0xFF424242),
                                shape = CircleShape,
                            )
                            .padding(10.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Mensagem") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        onSend(input)
                        input = ""
                    }
                },
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(text = "Enviar", fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}