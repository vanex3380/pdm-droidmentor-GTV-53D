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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Row(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {


            Box(modifier = Modifier.weight(1f)
                .padding(top=24.dp)){
                Button(onClick =  { onNavigate(Screen.TITLE) },shape = CircleShape){
                Text(text = "⬅\uFE0F",
                  textAlign = TextAlign.Center)
                }
            }


            Box(modifier = Modifier
                .weight(6f),
                contentAlignment = Alignment.Center){
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
                .weight(2f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally ) {
                Text(
                    text = "Cliente Android que conversa com o modelo Gemini, " +
                            "atuando como um mentor sénior de desenvolvimento Android.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                )
            }
        }
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Realizado por:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(text = "Tomás Duarte Nº52971", fontSize = 14.sp)
            Text(text = "Guilherme Nobre Nº52970", fontSize = 14.sp)
            Text(text = "Valentin Sukhar Nº52678", fontSize = 14.sp)
            }
        }

    }}