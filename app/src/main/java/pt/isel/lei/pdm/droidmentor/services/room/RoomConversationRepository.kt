package pt.isel.lei.pdm.droidmentor.services.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.services.ConversationRepository
import java.util.UUID

// Conversas guardadas em Room: sobrevivem a fechar a app
class RoomConversationRepository(
    private val dao: ConversationDao,
) : ConversationRepository {

    // Histórico: só id e título, da mais recente para a mais antiga
    override val conversations: Flow<List<Conversation>> =
        dao.getAll().map { entities -> entities.map { it.toConversation() } }

    override fun conversation(id: String): Flow<Conversation?> =
        dao.getWithMessages(id).map { it?.toConversation() }

    override suspend fun create(title: String): Conversation {
        val conversation = Conversation(id = UUID.randomUUID().toString(), title = title)
        dao.insertConversation(
            ConversationEntity(conversation.id, conversation.title, System.currentTimeMillis())
        )
        return conversation
    }

    override suspend fun addMessage(conversationId: String, message: Message) {
        dao.insertMessage(
            MessageEntity(conversationId = conversationId, role = message.role, text = message.text)
        )
    }
}
