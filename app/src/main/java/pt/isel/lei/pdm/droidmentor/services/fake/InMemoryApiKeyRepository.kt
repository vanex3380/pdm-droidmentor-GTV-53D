package pt.isel.lei.pdm.droidmentor.services.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.isel.lei.pdm.droidmentor.services.ApiKeyRepository

// Chave em memória (o DataStore substitui-o no passo 3b)
class InMemoryApiKeyRepository : ApiKeyRepository {
    private val key = MutableStateFlow<String?>(null)

    override val apiKey: Flow<String?> = key

    override suspend fun save(key: String) {
        this.key.value = key.ifBlank { null } // chave vazia conta como não configurada
    }
}
