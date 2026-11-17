package pt.isel.lei.pdm.droidmentor.services.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    // Flow: volta a emitir sempre que a tabela muda
    @Query("SELECT * FROM conversations ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ConversationEntity>>

    // Transaction: a conversa e as mensagens são lidas no mesmo estado da BD
    @Transaction
    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getWithMessages(id: String): Flow<ConversationWithMessages?>

    @Insert
    suspend fun insertConversation(conversation: ConversationEntity)

    @Insert
    suspend fun insertMessage(message: MessageEntity)
}
