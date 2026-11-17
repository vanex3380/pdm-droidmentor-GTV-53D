package pt.isel.lei.pdm.droidmentor.services.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.isel.lei.pdm.droidmentor.services.ApiKeyRepository

// Chave BYOK guardada em DataStore: sobrevive a fechar a app
class DataStoreApiKeyRepository(
    private val dataStore: DataStore<Preferences>,
) : ApiKeyRepository {

    // Emite de novo sempre que a chave muda
    override val apiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[API_KEY]?.ifBlank { null }
    }

    override suspend fun save(key: String) {
        dataStore.edit { preferences -> preferences[API_KEY] = key }
    }

    private companion object {
        val API_KEY = stringPreferencesKey("api_key") // nome do valor no ficheiro
    }
}
