package pt.isel.lei.pdm.droidmentor.services

// Contrato para saber se há rede antes de fazer um pedido
interface ConnectivityChecker {
    fun isOnline(): Boolean
}
