package pt.isel.lei.pdm.droidmentor.services.fake

import kotlinx.coroutines.delay
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role
import pt.isel.lei.pdm.droidmentor.services.ChatService

// Substitui o Gemini enquanto o cliente Ktor não existe (comportamento do M2)
class FakeChatService : ChatService {
    override suspend fun reply(history: List<Message>): Result<Message> {
        delay(1000) // simula a latência da rede
        return Result.success(Message(Role.MODEL, "Mensagem predefinida"))
    }
}
