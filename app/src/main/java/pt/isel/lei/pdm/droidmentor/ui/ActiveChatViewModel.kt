package pt.isel.lei.pdm.droidmentor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role
import pt.isel.lei.pdm.droidmentor.services.ActiveConversationRepository
import pt.isel.lei.pdm.droidmentor.services.ChatService
import pt.isel.lei.pdm.droidmentor.services.ConnectivityChecker
import pt.isel.lei.pdm.droidmentor.services.ConversationRepository
import pt.isel.lei.pdm.droidmentor.services.InvalidApiKeyException
import pt.isel.lei.pdm.droidmentor.services.MissingApiKeyException
import pt.isel.lei.pdm.droidmentor.services.NoConnectivityException
import pt.isel.lei.pdm.droidmentor.services.RateLimitExceededException
import pt.isel.lei.pdm.droidmentor.services.ServiceUnavailableException

// Máquina de estados do envio
sealed interface ChatStatus {
    data object Idle : ChatStatus
    data object Sending : ChatStatus
    data class Error(val message: String) : ChatStatus
}

// Tudo o que o ecrã precisa para se desenhar
data class ActiveChatUiState(
    val title: String = "Nova conversa",
    val messages: List<Message> = emptyList(), // mensagens guardadas
    val pending: Message? = null,              // pergunta à espera de resposta (ainda não guardada)
    val input: String = "",                    // texto na caixa de escrita
    val status: ChatStatus = ChatStatus.Idle,
)

class ActiveChatViewModel(
    private val repository: ConversationRepository,
    private val chatService: ChatService,
    private val activeConversation: ActiveConversationRepository,
    private val connectivity: ConnectivityChecker,
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveChatUiState()) // só o VM escreve
    val state: StateFlow<ActiveChatUiState> = _state.asStateFlow() // a UI só lê

    private var conversationId: String? = null // null = conversa ainda não criada
    private var observationJob: Job? = null    // observação da conversa aberta
    private var session = 0                    // muda sempre que se troca de conversa

    // Nova conversa: só é criada no repositório quando a primeira troca tiver sucesso
    fun startNew() = reset()

    // O utilizador saiu da conversa: deixa de ser a conversa ativa
    fun close() = reset()

    // Regra de arranque: true se havia uma conversa ativa e ela foi reaberta
    suspend fun restoreActiveConversation(): Boolean {
        val id = activeConversation.activeConversationId.first() ?: return false
        if (id == conversationId) return true // já está aberta (ex.: rotação)
        if (repository.conversation(id).first() == null) {
            activeConversation.clear() // a conversa já não existe
            return false
        }
        open(id)
        return true
    }

    // Abre uma conversa existente
    fun open(id: String) {
        session++
        _state.value = ActiveChatUiState()
        observe(id)
    }

    fun onInputChange(text: String) {
        _state.update { it.copy(input = text) }
    }

    fun send() {
        val text = _state.value.input.trim()
        if (text.isEmpty() || _state.value.status is ChatStatus.Sending) return // evita envio duplo

        // Requisito: verificar a rede antes de qualquer pedido; o texto fica na caixa
        if (!connectivity.isOnline()) {
            _state.update { it.copy(status = ChatStatus.Error(OFFLINE_MESSAGE)) }
            return
        }

        val question = Message(Role.USER, text)
        val id = conversationId
        val mySession = session
        _state.update { it.copy(input = "", pending = question, status = ChatStatus.Sending) }

        viewModelScope.launch { // sobrevive à rotação
            // Stateless: histórico guardado + a nova pergunta
            val saved = id?.let { repository.conversation(it).first()?.messages }.orEmpty()

            chatService.reply(saved + question)
                .onSuccess { answer ->
                    // Só agora se grava: a BD guarda sempre pares pergunta-resposta
                    val targetId = id ?: repository.create(text.take(TITLE_LENGTH)).id
                    repository.addMessage(targetId, question)
                    repository.addMessage(targetId, answer)
                    if (session == mySession) { // o utilizador ainda está nesta conversa
                        if (id == null) observe(targetId) // conversa acabada de criar
                        _state.update { it.copy(status = ChatStatus.Idle) }
                    }
                }
                .onFailure { error ->
                    // Nada foi gravado: a pergunta volta para a caixa para tentar de novo
                    if (session == mySession)
                        _state.update {
                            it.copy(
                                pending = null,
                                input = text,
                                status = ChatStatus.Error(error.toUserMessage()),
                            )
                        }
                }
        }
    }

    private fun reset() {
        session++
        observationJob?.cancel()
        conversationId = null
        _state.value = ActiveChatUiState()
        viewModelScope.launch { activeConversation.clear() } // limpa o registo
    }

    // Passa a observar a conversa: cada mudança no repositório atualiza o ecrã
    private fun observe(id: String) {
        observationJob?.cancel()
        conversationId = id
        observationJob = viewModelScope.launch {
            activeConversation.set(id) // regista-a como conversa ativa
            repository.conversation(id).collect { conversation ->
                if (conversation != null)
                    _state.update {
                        // A pergunta já está guardada: deixa de ser "pendente"
                        it.copy(title = conversation.title, messages = conversation.messages, pending = null)
                    }
            }
        }
    }

    private companion object {
        const val TITLE_LENGTH = 30
        const val OFFLINE_MESSAGE = "Sem ligação à rede. Tente novamente mais tarde."
    }
}

// Traduz a falha numa mensagem para o utilizador
private fun Throwable.toUserMessage(): String = when (this) {
    is MissingApiKeyException -> "Configura a chave da API nas definições"
    is InvalidApiKeyException -> "A chave da API não é válida"
    is RateLimitExceededException -> "Limite de pedidos atingido. Tenta daqui a pouco"
    is ServiceUnavailableException -> "O serviço está indisponível. Tenta mais tarde"
    is NoConnectivityException -> "Sem ligação, ou o servidor demorou demasiado"
    else -> "Não foi possível obter resposta"
}
