package pt.isel.lei.pdm.droidmentor.ui

import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleScreen(onNavigate: (Screen) -> Unit,
                onNewChat: () -> Unit) {

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
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier.weight(1f)
                    .padding(top = 24.dp)
            ) {
                Button(onClick = { onNavigate(Screen.ABOUT) }, shape = CircleShape) {
                    Text(
                        text = "ℹ\uFE0F",
                        textAlign = TextAlign.Center
                    )
                }
            }


            Box(
                modifier = Modifier
                    .weight(6f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DroidMentor",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
            Box(
                modifier = Modifier.weight(1f)
                    .padding(top = 24.dp)
            ) {
                Button(onClick = { onNavigate(Screen.SETTINGS) }, shape = CircleShape) {
                    Text(
                        text = "⚙\uFE0F",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center){
            Column(modifier = Modifier
                .padding(bottom = 150.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {onNewChat()}, modifier = Modifier.width(240.dp), shape = RectangleShape) {
                    Text(
                        text = "New Chat",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp
                    )
                }
                Button(onClick = {onNavigate(Screen.CHAT_HISTORY)}, modifier = Modifier.width(240.dp).padding(top=8.dp), shape = RectangleShape) {
                    Text(
                        text = "Chat History",
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp

                    )
                }


            }
        }


    }
}