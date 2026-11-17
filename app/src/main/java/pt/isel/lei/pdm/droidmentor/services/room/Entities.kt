package pt.isel.lei.pdm.droidmentor.services.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import pt.isel.lei.pdm.droidmentor.domain.Conversation
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role

// Tabela das conversas
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long, // para ordenar o histórico
)

// Tabela das mensagens: cada uma pertence a uma conversa
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE, // apagar a conversa apaga as mensagens
        )
    ],
    indices = [Index("conversationId")], // acelera a procura das mensagens de uma conversa
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // automático: dá a ordem das mensagens
    val conversationId: String,
    val role: Role, // guardado como texto ("USER" / "MODEL")
    val text: String,
)

// Uma conversa com as suas mensagens (relação 1-para-N)
data class ConversationWithMessages(
    @Embedded val conversation: ConversationEntity,
    @Relation(parentColumn = "id", entityColumn = "conversationId")
    val messages: List<MessageEntity>,
)

// Entidade -> domínio, sem mensagens (para o histórico)
fun ConversationEntity.toConversation() = Conversation(id = id, title = title)

// Entidade -> domínio, com as mensagens pela ordem em que foram inseridas
fun ConversationWithMessages.toConversation() = Conversation(
    id = conversation.id,
    title = conversation.title,
    messages = messages.sortedBy { it.id }.map { Message(it.role, it.text) },
)
