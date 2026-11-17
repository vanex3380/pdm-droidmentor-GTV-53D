package pt.isel.lei.pdm.droidmentor.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pt.isel.lei.pdm.droidmentor.MainDispatcherRule
import pt.isel.lei.pdm.droidmentor.domain.Message
import pt.isel.lei.pdm.droidmentor.domain.Role
import pt.isel.lei.pdm.droidmentor.services.ChatService
import pt.isel.lei.pdm.droidmentor.services.RateLimitExceededException
import pt.isel.lei.pdm.droidmentor.services.fake.FakeConnectivityChecker
import pt.isel.lei.pdm.droidmentor.services.fake.InMemoryActiveConversationRepository
import pt.isel.lei.pdm.droidmentor.services.fake.InMemoryConversationRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveChatViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // ChatService controlado pelo teste: devolve o resultado escolhido e regista os pedidos
    private class StubChatService(var result: Result<Message>) : ChatService {
        val requests = mutableListOf<List<Message>>()

        override suspend fun reply(history: List<Message>): Result<Message> {
            requests += history
            return result
        }
    }

    private val answer = Message(Role.MODEL, "Resposta")
    private val repository = InMemoryConversationRepository() // começa com as conversas "1" e "2"
    private val chat = StubChatService(Result.success(answer))
    private val active = InMemoryActiveConversationRepository()
    private val connectivity = FakeConnectivityChecker(online = true)
    private val vm = ActiveChatViewModel(repository, chat, active, connectivity)

    @Test
    fun `without network it shows an error, keeps the text and makes no request`() = runTest {
        connectivity.online = false
        vm.startNew()
        vm.onInputChange("Olá")

        vm.send()

        val state = vm.state.value
        assertEquals("Olá", state.input)
        assertTrue(state.status is ChatStatus.Error)
        assertTrue(chat.requests.isEmpty())
        assertEquals(2, repository.conversations.first().size) // nada foi criado
    }

    @Test
    fun `successful send in a new conversation saves the question and the answer`() = runTest {
        vm.startNew()
        vm.onInputChange("O que é um ViewModel?")

        vm.send()
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(listOf(Message(Role.USER, "O que é um ViewModel?"), answer), state.messages)
        assertNull(state.pending)
        assertEquals(ChatStatus.Idle, state.status)
        assertEquals("O que é um ViewModel?", state.title) // título tirado da 1.ª mensagem
        assertEquals(3, repository.conversations.first().size)
        assertNotNull(active.activeConversationId.first()) // registada como conversa ativa
    }

    @Test
    fun `failed send saves nothing and puts the text back in the input`() = runTest {
        chat.result = Result.failure(RateLimitExceededException())
        vm.startNew()
        vm.onInputChange("Olá")

        vm.send()
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals("Olá", state.input)
        assertNull(state.pending)
        assertTrue(state.status is ChatStatus.Error)
        assertTrue(state.messages.isEmpty())
        assertEquals(2, repository.conversations.first().size) // nenhuma conversa vazia
    }

    @Test
    fun `send in an existing conversation sends the full history`() = runTest {
        vm.open("1") // "Abecedário", com 4 mensagens
        vm.onInputChange("E depois?")

        vm.send()
        advanceUntilIdle()

        val sent = chat.requests.single()
        assertEquals(5, sent.size) // stateless: 4 guardadas + a nova pergunta
        assertEquals(Message(Role.USER, "E depois?"), sent.last())
        assertEquals(6, vm.state.value.messages.size) // + pergunta e resposta guardadas
    }

    @Test
    fun `closing the conversation clears the active record`() = runTest {
        vm.open("1")
        assertEquals("1", active.activeConversationId.first())

        vm.close()

        assertNull(active.activeConversationId.first())
    }

    @Test
    fun `restore reopens the active conversation`() = runTest {
        active.set("1")

        assertTrue(vm.restoreActiveConversation())
        assertEquals("Abecedário", vm.state.value.title)
    }

    @Test
    fun `restore with a conversation that no longer exists clears the record`() = runTest {
        active.set("nao-existe")

        assertFalse(vm.restoreActiveConversation())
        assertNull(active.activeConversationId.first())
    }
}
