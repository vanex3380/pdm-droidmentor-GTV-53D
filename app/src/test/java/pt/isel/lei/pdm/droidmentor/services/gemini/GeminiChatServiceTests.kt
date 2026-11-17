package pt.isel.lei.pdm.droidmentor.services.gemini

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role
import pt.isel.lei.pdm.droidmentor.services.InvalidApiKeyException
import pt.isel.lei.pdm.droidmentor.services.MissingApiKeyException
import pt.isel.lei.pdm.droidmentor.services.NoConnectivityException
import pt.isel.lei.pdm.droidmentor.services.RateLimitExceededException
import pt.isel.lei.pdm.droidmentor.services.ServiceUnavailableException
import pt.isel.lei.pdm.droidmentor.services.UnexpectedChatException
import pt.isel.lei.pdm.droidmentor.services.fake.InMemoryApiKeyRepository
import java.io.IOException

class GeminiChatServiceTests {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val okBody =
        """{"candidates":[{"content":{"role":"model","parts":[{"text":"Um objeto"}]}}]}"""
    private val history = listOf(Message(Role.USER, "O que é um ViewModel?"))

    // Cria o serviço ligado a um servidor simulado (MockEngine)
    private suspend fun service(
        apiKey: String? = "test-key",
        handler: MockRequestHandler,
    ): GeminiChatService {
        val client = HttpClient(MockEngine(handler)) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val keys = InMemoryApiKeyRepository()
        if (apiKey != null) keys.save(apiKey)
        return GeminiChatService(client, keys)
    }

    @Test
    fun `200 returns the model message`() = runTest {
        val service = service { respond(okBody, HttpStatusCode.OK, jsonHeaders) }

        val result = service.reply(history)

        assertEquals(Message(Role.MODEL, "Um objeto"), result.getOrNull())
    }

    @Test
    fun `request has the key in the header, the persona and the full history`() = runTest {
        var body = ""
        var url = ""
        var key: String? = null
        val service = service { request ->
            body = request.body.toByteArray().decodeToString()
            url = request.url.toString()
            key = request.headers["x-goog-api-key"]
            respond(okBody, HttpStatusCode.OK, jsonHeaders)
        }
        val conversation = listOf(
            Message(Role.USER, "Pergunta 1"),
            Message(Role.MODEL, "Resposta 1"),
            Message(Role.USER, "Pergunta 2"),
        )

        service.reply(conversation)

        assertEquals("test-key", key)
        assertFalse("A chave não pode ir no URL", url.contains("test-key"))
        assertTrue(body.contains("\"system_instruction\""))
        assertTrue(body.contains("Pergunta 1") && body.contains("Resposta 1") && body.contains("Pergunta 2"))
        assertTrue(body.contains("\"role\":\"model\""))
    }

    @Test
    fun `without a key it fails without making a request`() = runTest {
        var requests = 0
        val service = service(apiKey = null) {
            requests++
            respond(okBody, HttpStatusCode.OK, jsonHeaders)
        }

        val result = service.reply(history)

        assertTrue(result.exceptionOrNull() is MissingApiKeyException)
        assertEquals(0, requests)
    }

    @Test
    fun `400 API_KEY_INVALID maps to InvalidApiKeyException`() = runTest {
        val service = service {
            respond(
                """{"error":{"code":400,"status":"INVALID_ARGUMENT","details":[{"reason":"API_KEY_INVALID"}]}}""",
                HttpStatusCode.BadRequest,
                jsonHeaders,
            )
        }

        assertTrue(service.reply(history).exceptionOrNull() is InvalidApiKeyException)
    }

    @Test
    fun `429 maps to RateLimitExceededException`() = runTest {
        val service = service { respond("{}", HttpStatusCode.TooManyRequests, jsonHeaders) }

        assertTrue(service.reply(history).exceptionOrNull() is RateLimitExceededException)
    }

    @Test
    fun `503 maps to ServiceUnavailableException`() = runTest {
        val service = service { respond("{}", HttpStatusCode.ServiceUnavailable, jsonHeaders) }

        assertTrue(service.reply(history).exceptionOrNull() is ServiceUnavailableException)
    }

    @Test
    fun `200 without candidates maps to UnexpectedChatException`() = runTest {
        val service = service { respond("""{"candidates":[]}""", HttpStatusCode.OK, jsonHeaders) }

        assertTrue(service.reply(history).exceptionOrNull() is UnexpectedChatException)
    }

    @Test
    fun `network failure maps to NoConnectivityException`() = runTest {
        val service = service { throw IOException("sem rede") }

        assertTrue(service.reply(history).exceptionOrNull() is NoConnectivityException)
    }
}
