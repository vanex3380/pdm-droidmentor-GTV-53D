package pt.isel.lei.pdm.droidmentor.services

import kotlinx.coroutines.flow.Flow

// Contrato para a chave BYOK
interface ApiKeyRepository {
    // null = chave ainda não configurada
    val apiKey: Flow<String?>

    suspend fun save(key: String)
}
