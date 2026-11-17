package pt.isel.lei.pdm.droidmentor.services

import kotlinx.coroutines.flow.Flow
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Message

// Contrato para guardar e ler conversas (fonte de verdade offline)
interface ConversationRepository {
    // Flow: quem observa recebe cada alteração automaticamente
    val conversations: Flow<List<Conversation>>

    // null se a conversa não existir
    fun conversation(id: String): Flow<Conversation?>

    suspend fun create(title: String): Conversation

    suspend fun addMessage(conversationId: String, message: Message)
}
