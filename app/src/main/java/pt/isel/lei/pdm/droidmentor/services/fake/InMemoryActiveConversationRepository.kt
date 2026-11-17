package pt.isel.lei.pdm.droidmentor.services.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.lei.pdm.droidmentor.services.ActiveConversationRepository

// Conversa ativa em memória (para testes)
class InMemoryActiveConversationRepository : ActiveConversationRepository {
    private val id = MutableStateFlow<String?>(null)

    override val activeConversationId: Flow<String?> = id

    override suspend fun set(id: String) {
        this.id.value = id
    }

    override suspend fun clear() {
        id.value = null
    }
}
