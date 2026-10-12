package pt.isel.lei.pdm.droidmentor.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationTests {

    private val base = Conversation(
        id = "1",
        title = "Teste",
        messages = listOf(
            Message(Role.USER, "Olá"),
            Message(Role.MODEL, "Viva!"),
        ),
    )

    @Test
    fun `addMessage acrescenta no fim`() {
        val r = base.addMessage(Message(Role.USER, "Nova"))
        assertEquals(3, r.messages.size)
        assertEquals("Nova", r.messages.last().text)
    }

    @Test
    fun `addMessage nao altera a conversa original`() {
        base.addMessage(Message(Role.USER, "Nova"))
        assertEquals(2, base.messages.size)
    }

    @Test
    fun `addMessage preserva o papel da mensagem`() {
        val r = base.addMessage(Message(Role.MODEL, "Resposta"))
        assertEquals(Role.MODEL, r.messages.last().role)
    }
}