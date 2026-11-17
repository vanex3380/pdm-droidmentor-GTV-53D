package pt.isel.lei.pdm.droidmentor

import android.app.Application
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pt.isel.lei.pdm.droidmentor.services.ActiveConversationRepository
import pt.isel.lei.pdm.droidmentor.services.ApiKeyRepository
import pt.isel.lei.pdm.droidmentor.services.ChatService
import pt.isel.lei.pdm.droidmentor.services.ConnectivityChecker
import pt.isel.lei.pdm.droidmentor.services.ConversationRepository
import pt.isel.lei.pdm.droidmentor.services.connectivity.AndroidConnectivityChecker
import pt.isel.lei.pdm.droidmentor.services.datastore.DataStoreActiveConversationRepository
import pt.isel.lei.pdm.droidmentor.services.datastore.DataStoreApiKeyRepository
import pt.isel.lei.pdm.droidmentor.services.gemini.GeminiChatService
import pt.isel.lei.pdm.droidmentor.services.room.AppDatabase
import pt.isel.lei.pdm.droidmentor.services.room.RoomConversationRepository

// Service Locator: o que a app disponibiliza, sem dizer como é construído
interface DependencyContainer {
    val chatService: ChatService
    val conversationRepository: ConversationRepository
    val apiKeyRepository: ApiKeyRepository
    val activeConversationRepository: ActiveConversationRepository
    val connectivityChecker: ConnectivityChecker
}

// Uma instância por processo, criada antes de qualquer Activity (registada no manifest)
// Só cria e entrega dependências; estado de UI não pertence aqui
class DroidMentorApplication : Application(), DependencyContainer {

    // Ficheiro de preferências da app (uma única instância)
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Base de dados única para toda a app
    private val database by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "droidmentor-db")
            .fallbackToDestructiveMigration(true) // se o formato mudar, recria a BD
            .build()
    }

    // Cliente HTTP único para toda a app
    private val httpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }) // a resposta tem campos que não declaramos
            }
            install(HttpTimeout) {
                // O modelo pode demorar mais do que os 10 s por omissão
                requestTimeoutMillis = 60_000
                socketTimeoutMillis = 60_000
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
                sanitizeHeader { it == GeminiChatService.API_KEY_HEADER } // a chave não vai para o Logcat
            }
        }
    }

    // lazy: criado no primeiro uso e reutilizado (instância única)
    // Trocar fake -> real = mudar só a linha correspondente
    override val chatService: ChatService by lazy {
        GeminiChatService(httpClient, apiKeyRepository)
    }

    override val conversationRepository: ConversationRepository by lazy {
        RoomConversationRepository(database.conversationDao())
    }

    override val apiKeyRepository: ApiKeyRepository by lazy {
        DataStoreApiKeyRepository(dataStore)
    }

    override val activeConversationRepository: ActiveConversationRepository by lazy {
        DataStoreActiveConversationRepository(dataStore)
    }

    override val connectivityChecker: ConnectivityChecker by lazy {
        AndroidConnectivityChecker(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate") // confirma no Logcat que a nossa Application foi usada
    }

    companion object {
        const val TAG = "DroidMentorApp"
    }
}
