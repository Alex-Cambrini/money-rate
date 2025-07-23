package it.unibo.domain.usecase.home

/**
 * Use case che converte una stringa in numero e la moltiplica per un tasso di cambio.
 * Restituisce null se la stringa non è un numero valido o se il tasso è null.
 */
class CalculateConversionUseCase {
    /**
     * Converte l'importo (String) e moltiplica per il tasso.
     * @param amount importo in String
     * @param rate tasso di conversione nullable
     * @return conversione o null se input non validi
     */
    operator fun invoke(amount: String, rate: Double?): Double? {
        val parsed = amount.toDoubleOrNull()
        return if (parsed != null && rate != null) parsed * rate else null
    }
}
