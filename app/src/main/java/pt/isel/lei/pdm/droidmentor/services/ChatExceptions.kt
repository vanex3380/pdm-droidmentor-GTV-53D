package pt.isel.lei.pdm.droidmentor.services

// Falhas possíveis ao pedir uma resposta ao modelo
sealed class ChatServiceException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

// Ainda não foi configurada uma chave
class MissingApiKeyException : ChatServiceException("API key not configured")

// A chave foi rejeitada (400 API_KEY_INVALID, 401, 403)
class InvalidApiKeyException : ChatServiceException("API key rejected")

// Limite de pedidos excedido (429)
class RateLimitExceededException : ChatServiceException("Rate limit exceeded")

// Problema do lado do servidor (500, 503, 504)
class ServiceUnavailableException(message: String) : ChatServiceException(message)

// O pedido não chegou ao servidor (sem rede, timeout)
class NoConnectivityException(cause: Throwable) : ChatServiceException("Network error", cause)

// Qualquer outro caso (resposta sem texto, JSON inesperado, modelo inexistente...)
class UnexpectedChatException(message: String, cause: Throwable? = null) :
    ChatServiceException(message, cause)
