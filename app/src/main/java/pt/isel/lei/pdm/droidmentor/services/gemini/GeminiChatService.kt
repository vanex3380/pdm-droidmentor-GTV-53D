package pt.isel.lei.pdm.droidmentor.services.gemini

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.services.ApiKeyRepository
import pt.isel.lei.pdm.droidmentor.services.ChatService
import pt.isel.lei.pdm.droidmentor.services.InvalidApiKeyException
import pt.isel.lei.pdm.droidmentor.services.MissingApiKeyException
import pt.isel.lei.pdm.droidmentor.services.NoConnectivityException
import pt.isel.lei.pdm.droidmentor.services.RateLimitExceededException
import pt.isel.lei.pdm.droidmentor.services.ServiceUnavailableException
import pt.isel.lei.pdm.droidmentor.services.UnexpectedChatException
import java.io.IOException

// Implementação real do ChatService: Gemini via REST (generateContent)
class GeminiChatService(
    private val http: HttpClient,
    private val apiKeyRepository: ApiKeyRepository,
) : ChatService {

    override suspend fun reply(history: List<Message>): Result<Message> = try {
        val key = apiKeyRepository.apiKey.first() // chave atual (BYOK)
        if (key == null) {
            Result.failure(MissingApiKeyException())
        } else {
            val response = http.post(URL) {
                header(API_KEY_HEADER, key) // no cabeçalho, não no URL
                contentType(ContentType.Application.Json)
                // Stateless: persona + histórico completo em cada pedido
                setBody(
                    GenerateContentRequest(
                        systemInstruction = ContentDto(parts = listOf(PartDto(PERSONA))),
                        contents = history.map { it.toDto() },
                    )
                )
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val message = response.body<GenerateContentResponse>().toMessage()
                    if (message != null) Result.success(message)
                    else Result.failure(UnexpectedChatException("Response without text"))
                }
                // O Gemini devolve 400 tanto para chave inválida como para pedido mal formado
                HttpStatusCode.BadRequest ->
                    if (response.bodyAsText().contains("API_KEY_INVALID"))
                        Result.failure(InvalidApiKeyException())
                    else
                        Result.failure(UnexpectedChatException("API returned ${response.status}"))
                HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden ->
                    Result.failure(InvalidApiKeyException())
                HttpStatusCode.TooManyRequests ->
                    Result.failure(RateLimitExceededException())
                HttpStatusCode.InternalServerError,
                HttpStatusCode.ServiceUnavailable,
                HttpStatusCode.GatewayTimeout ->
                    Result.failure(ServiceUnavailableException("API returned ${response.status}"))
                else ->
                    Result.failure(UnexpectedChatException("API returned ${response.status}"))
            }
        }
    } catch (e: IOException) {
        // O pedido não chegou ao servidor ou a ligação caiu a meio
        Result.failure(NoConnectivityException(e))
    } catch (e: Exception) {
        if (e is CancellationException) throw e // o cancelamento tem de se propagar
        Result.failure(UnexpectedChatException("Unexpected error", e))
    }

    companion object {
        const val API_KEY_HEADER = "x-goog-api-key"

        private const val MODEL = "gemini-3.1-flash-lite"
        private const val URL =
            "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

        private const val PERSONA =
            "És o DroidMentor, um engenheiro Android sénior que orienta programadores. " +
                "Dá conselhos práticos sobre boas práticas de desenvolvimento Android " +
                "(Kotlin, Jetpack Compose, arquitetura, testes). " +
                "Explica o porquê das recomendações, com exemplos curtos quando ajudar. " +
                "Responde na língua do utilizador."
    }
}
