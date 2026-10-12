package pt.isel.lei.pdm.droidmentor.domain

data class Conversation(
    val id: String,
    val title: String,
    val messages: List<Message> = emptyList(),
) {
    fun addMessage(message: Message): Conversation =
        copy(messages = messages + message)
}