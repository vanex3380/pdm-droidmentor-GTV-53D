package pt.isel.lei.pdm.droidmentor.services.fake

import pt.isel.lei.pdm.droidmentor.services.ConnectivityChecker

// Para os testes: o teste decide se há rede
class FakeConnectivityChecker(var online: Boolean = true) : ConnectivityChecker {
    override fun isOnline(): Boolean = online
}
