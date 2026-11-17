package pt.isel.lei.pdm.droidmentor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.services.ConversationRepository

class ChatHistoryViewModel(repository: ConversationRepository) : ViewModel() {

    // Lista de conversas: atualiza-se sempre que o repositório muda
    val conversations: StateFlow<List<Conversation>> =
        repository.conversations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // só lê enquanto o ecrã observa
            initialValue = emptyList(),
        )
}
