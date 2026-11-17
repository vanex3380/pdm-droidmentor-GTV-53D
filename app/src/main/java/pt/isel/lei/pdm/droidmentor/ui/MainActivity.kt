package pt.isel.lei.pdm.droidmentor.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import pt.isel.lei.pdm.droidmentor.DependencyContainer
import pt.isel.lei.pdm.droidmentor.ui.common.viewModelInit
import pt.isel.lei.pdm.droidmentor.ui.theme.DroidMentorTheme

class MainActivity : ComponentActivity() {

    // Service Locator: a Application entrega as dependências
    private val container by lazy { application as DependencyContainer }

    // Os ViewModels sobrevivem à rotação; a lambda só corre na primeira criação
    private val historyVm by viewModels<ChatHistoryViewModel> {
        viewModelInit { ChatHistoryViewModel(container.conversationRepository) }
    }
    private val chatVm by viewModels<ActiveChatViewModel> {
        viewModelInit {
            ActiveChatViewModel(
                container.conversationRepository,
                container.chatService,
                container.activeConversationRepository,
                container.connectivityChecker,
            )
        }
    }
    private val settingsVm by viewModels<SettingsViewModel> {
        viewModelInit { SettingsViewModel(container.apiKeyRepository) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidMentorTheme {
                DroidMentorApp(historyVm, chatVm, settingsVm)
            }
        }
    }
}
