package pt.isel.lei.pdm.droidmentor.services.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.isel.lei.pdm.droidmentor.services.ActiveConversationRepository

// Id da conversa ativa guardado em DataStore
class DataStoreActiveConversationRepository(
    private val dataStore: DataStore<Preferences>,
) : ActiveConversationRepository {

    override val activeConversationId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACTIVE_ID]
    }

    override suspend fun set(id: String) {
        dataStore.edit { preferences -> preferences[ACTIVE_ID] = id }
    }

    override suspend fun clear() {
        dataStore.edit { preferences -> preferences.remove(ACTIVE_ID) }
    }

    private companion object {
        val ACTIVE_ID = stringPreferencesKey("active_conversation_id")
    }
}
