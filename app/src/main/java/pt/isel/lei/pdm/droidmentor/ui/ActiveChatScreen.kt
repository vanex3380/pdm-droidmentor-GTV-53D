package pt.isel.lei.pdm.droidmentor.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pt.isel.lei.pdm.droidmentor.domain.Role

@Composable
fun ActiveChatScreen(
    vm: ActiveChatViewModel,
    onNavigate: (Screen) -> Unit,
) {
    // O ecrã desenha o estado do VM e reporta-lhe os eventos
    val state by vm.state.collectAsStateWithLifecycle()

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
            Button(
                onClick = { onNavigate(Screen.CHAT_HISTORY) },
                modifier = Modifier.width(50.dp).padding(top = 24.dp),
                shape = CircleShape,
            ) {
                Text(text = "⬅️", textAlign = TextAlign.Center)
            }
            Text(
                text = state.title,
                modifier = Modifier.padding(top = 24.dp, start = 12.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()), // respostas longas fazem scroll
        ) {
            // mensagens guardadas + a pergunta à espera de resposta
            val bubbles = state.messages + listOfNotNull(state.pending)

            bubbles.forEach { msg ->
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
                            .alpha(if (msg === state.pending) 0.6f else 1f) // ainda não guardada
                            .background(
                                color = if (isUser) Color(0xFF2962FF) else Color(0xFF424242),
                                shape = RoundedCornerShape(16.dp), // aguenta várias linhas
                            )
                            .padding(10.dp),
                    )
                }
            }
        }

        // Feedback do envio: depende do estado da máquina de estados
        when (val status = state.status) {
            ChatStatus.Idle -> {}
            ChatStatus.Sending -> SpinningGear(modifier = Modifier.padding(bottom = 8.dp))
            is ChatStatus.Error -> Text(
                text = status.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = state.input,
                onValueChange = vm::onInputChange,
                label = { Text("Mensagem") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            Button(
                onClick = vm::send,
                enabled = state.status !is ChatStatus.Sending, // não envia duas vezes
                modifier = Modifier.fillMaxHeight(),
            ) {
                Text(text = "Enviar", fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}

// Engrenagem a rodar enquanto o modelo responde
@Composable
private fun SpinningGear(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "gear")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart, // volta a 0° e continua a rodar
        ),
        label = "gearAngle",
    )
    Text(text = "⚙️", fontSize = 28.sp, modifier = modifier.rotate(angle))
}
