package pt.isel.lei.pdm.droidmentor.services

import pt.isel.lei.pdm.droidmentor.domain.Message

// Contrato para obter a resposta do modelo
interface ChatService {
    // Stateless: recebe sempre o histórico completo (user/model alternados)
    // Result torna a falha explícita (sem rede, 429, 500...) em vez de lançar exceção
    suspend fun reply(history: List<Message>): Result<Message>
}
