package pt.isel.lei.pdm.droidmentor.services.room

import androidx.room.Database
import androidx.room.RoomDatabase

// A base de dados: junta as tabelas e entrega o DAO
@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}
