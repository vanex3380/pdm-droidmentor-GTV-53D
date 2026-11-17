package pt.isel.lei.pdm.droidmentor.services.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role

// Corpo do pedido ao generateContent
@Serializable
data class GenerateContentRequest(
    @SerialName("system_instruction") // nome do campo no JSON
    val systemInstruction: ContentDto,
    val contents: List<ContentDto>,
)

// Uma mensagem: quem falou e o que disse
@Serializable
data class ContentDto(
    val role: String? = null, // "user" ou "model"; ausente no system_instruction
    val parts: List<PartDto> = emptyList(),
)

@Serializable
data class PartDto(val text: String? = null)

// Corpo da resposta: só declaramos os campos de que precisamos
@Serializable
data class GenerateContentResponse(val candidates: List<CandidateDto> = emptyList())

@Serializable
data class CandidateDto(val content: ContentDto? = null)

// Domínio -> DTO
fun Message.toDto() = ContentDto(
    role = when (role) {
        Role.USER -> "user"
        Role.MODEL -> "model"
    },
    parts = listOf(PartDto(text)),
)

// DTO -> domínio; null se o modelo não devolveu texto (ex.: resposta bloqueada)
fun GenerateContentResponse.toMessage(): Message? {
    val text = candidates.firstOrNull()?.content?.parts
        ?.mapNotNull { it.text }
        ?.joinToString("")
    return if (text.isNullOrBlank()) null else Message(Role.MODEL, text)
}
