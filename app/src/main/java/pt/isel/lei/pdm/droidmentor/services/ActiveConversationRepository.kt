package pt.isel.lei.pdm.droidmentor.services

import kotlinx.coroutines.flow.Flow

// Contrato para saber em que conversa o utilizador estava (regra de arranque)
interface ActiveConversationRepository {
    // null = o utilizador não está em nenhuma conversa
    val activeConversationId: Flow<String?>

    suspend fun set(id: String)

    suspend fun clear()
}
