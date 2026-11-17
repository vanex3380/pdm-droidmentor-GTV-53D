package pt.isel.lei.pdm.droidmentor.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

enum class Screen { TITLE, SETTINGS, ABOUT, CHAT_HISTORY, ACTIVE_CHAT }

@Composable
fun DroidMentorApp(
    historyVm: ChatHistoryViewModel,
    chatVm: ActiveChatViewModel,
    settingsVm: SettingsViewModel,
) {
    // Só navegação: os dados vivem nos ViewModels
    var currentScreen by rememberSaveable { mutableStateOf(Screen.TITLE) }

    // Regra de arranque: se havia uma conversa ativa, vai direto para ela
    LaunchedEffect(Unit) {
        if (chatVm.restoreActiveConversation()) currentScreen = Screen.ACTIVE_CHAT
    }

    // Botão voltar do sistema: faz o mesmo que a seta de cada ecrã
    // No Title fica desativado, e o sistema fecha a app
    BackHandler(enabled = currentScreen != Screen.TITLE) {
        if (currentScreen == Screen.ACTIVE_CHAT) {
            chatVm.close()
            currentScreen = Screen.CHAT_HISTORY
        } else {
            currentScreen = Screen.TITLE
        }
    }

    Surface {
        when (currentScreen) {
            Screen.TITLE -> TitleScreen(
                onNavigate = { currentScreen = it },
                onNewChat = {
                    chatVm.startNew()
                    currentScreen = Screen.ACTIVE_CHAT
                },
            )
            Screen.SETTINGS -> SettingsScreen(
                vm = settingsVm,
                onNavigate = { currentScreen = it },
            )
            Screen.ABOUT -> AboutScreen(onNavigate = { currentScreen = it })
            Screen.CHAT_HISTORY -> ChatHistoryScreen(
                vm = historyVm,
                onNavigate = { currentScreen = it },
                onOpen = { id ->
                    chatVm.open(id) // diz ao VM do chat que conversa mostrar
                    currentScreen = Screen.ACTIVE_CHAT
                },
            )
            Screen.ACTIVE_CHAT -> ActiveChatScreen(
                vm = chatVm,
                onNavigate = {
                    chatVm.close() // saiu da conversa pela seta
                    currentScreen = it
                },
            )
        }
    }
}
