package pt.isel.lei.pdm.droidmentor.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role

enum class Screen { TITLE, SETTINGS, ABOUT, CHAT_HISTORY, ACTIVE_CHAT }

val fakeConversations = listOf(
    Conversation(
        id = "1",
        title = "Abecedário",
        messages = listOf(
            Message(Role.USER, "A"),
            Message(Role.MODEL, "B"),
            Message(Role.USER, "C"),
            Message(Role.MODEL, "D"),
        ),
    ),
    Conversation(
        id = "2",
        title = "Contagem",
        messages = listOf(
            Message(Role.USER, "1"),
            Message(Role.MODEL, "2"),
            Message(Role.USER,"3"),
            Message(Role.MODEL,"4")
        ),
    ),
)



@Composable
fun DroidMentorApp() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.TITLE) }
    var activeConversation by remember { mutableStateOf<Conversation?>(null) }
Surface() {
    when (currentScreen) {
        Screen.TITLE        -> TitleScreen(
            onNavigate = { currentScreen = it },
            onNewChat = {activeConversation = Conversation(id = java.util.UUID.randomUUID().toString(), title = "Nova Conversa")
                         currentScreen = Screen.ACTIVE_CHAT
            },
        )
        Screen.SETTINGS     -> SettingsScreen(onNavigate = { currentScreen = it })
        Screen.ABOUT        -> AboutScreen(onNavigate = { currentScreen = it })
        Screen.CHAT_HISTORY -> ChatHistoryScreen(
                onNavigate = { currentScreen = it},
                conversations = fakeConversations,
                onOpen = { conversa ->
                    activeConversation = conversa
                    currentScreen = Screen.ACTIVE_CHAT
        })
        Screen.ACTIVE_CHAT  -> ActiveChatScreen(
            onNavigate = {currentScreen = it},
            conversation = activeConversation,
            onSend = {text -> activeConversation = activeConversation
                ?.addMessage(Message(Role.USER, text))
                ?.addMessage(Message(Role.MODEL,"Mensagem predefinida"))}
        )
    }
}
}

