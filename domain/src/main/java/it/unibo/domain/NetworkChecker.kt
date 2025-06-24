package it.unibo.domain

interface NetworkChecker {
    fun isNetworkAvailable(): Boolean
}