package pt.isel.lei.pdm.droidmentor.services.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role
import pt.isel.lei.pdm.droidmentor.services.ConversationRepository
import java.util.UUID

// Repositório em memória: perde tudo ao fechar a app (o Room substitui-o no passo 3c)
class InMemoryConversationRepository : ConversationRepository {

    // Estado atual: id -> conversa. StateFlow avisa os observadores a cada mudança
    private val store = MutableStateFlow(sampleConversations.associateBy { it.id })

    override val conversations: Flow<List<Conversation>> =
        store.map { it.values.toList() }

    override fun conversation(id: String): Flow<Conversation?> =
        store.map { it[id] }

    override suspend fun create(title: String): Conversation {
        val conversation = Conversation(id = UUID.randomUUID().toString(), title = title)
        store.update { it + (conversation.id to conversation) } // update é atómico
        return conversation
    }

    override suspend fun addMessage(conversationId: String, message: Message) {
        store.update { current ->
            val conversation = current[conversationId] ?: return@update current
            current + (conversationId to conversation.addMessage(message)) // domínio imutável
        }
    }
}

// Dados iniciais (os mesmos de DroidMentorApp.fakeConversations)
private val sampleConversations = listOf(
    Conversation(
        id = "1",
        title = "Abecedário",
        messages = listOf(
            Message(Role.USER, "A"), Message(Role.MODEL, "B"),
            Message(Role.USER, "C"), Message(Role.MODEL, "D"),
        ),
    ),
    Conversation(
        id = "2",
        title = "Contagem",
        messages = listOf(
            Message(Role.USER, "1"), Message(Role.MODEL, "2"),
            Message(Role.USER, "3"), Message(Role.MODEL, "4"),
        ),
    ),
)
