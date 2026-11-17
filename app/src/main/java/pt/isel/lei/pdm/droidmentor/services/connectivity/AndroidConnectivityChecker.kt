package pt.isel.lei.pdm.droidmentor.services.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import pt.isel.lei.pdm.droidmentor.services.ConnectivityChecker

// Pergunta ao sistema se há uma rede ativa com acesso à Internet
// Recebe o Context da Application (vive o processo todo, não causa leaks)
class AndroidConnectivityChecker(context: Context) : ConnectivityChecker {

    private val manager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isOnline(): Boolean {
        val network = manager.activeNetwork ?: return false        // nenhuma rede ligada
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
