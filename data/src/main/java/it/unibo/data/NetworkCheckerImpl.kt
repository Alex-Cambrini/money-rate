package it.unibo.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import it.unibo.domain.NetworkChecker

/**
 * Implementazione di NetworkChecker che verifica la connessione a Internet.
 * Usa ConnectivityManager per controllare se è presente una rete con accesso Internet.
 */
class NetworkCheckerImpl(private val context: Context) : NetworkChecker {

    /**
     * Ritorna true se il dispositivo è connesso a una rete con accesso Internet.
     */
    override fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
