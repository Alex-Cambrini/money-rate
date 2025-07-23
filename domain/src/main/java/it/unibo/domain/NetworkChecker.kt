package it.unibo.domain

/**
 * Controlla la disponibilità della rete.
 */
interface NetworkChecker {
    /**
     * Verifica se la rete è disponibile.
     * @return true se la rete è attiva, altrimenti false.
     */
    fun isNetworkAvailable(): Boolean
}
