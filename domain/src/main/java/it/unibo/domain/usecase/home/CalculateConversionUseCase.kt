package it.unibo.domain.usecase.home

class CalculateConversionUseCase {
    /**
     * Prova a convertire l'importo e moltiplica per il tasso se validi.
     * @param amount importo in String
     * @param rate tasso di conversione nullable
     * @return conversione o null se input non validi
     */
    operator fun invoke(amount: String, rate: Double?): Double? {
        val parsed = amount.toDoubleOrNull() // prova a convertire String in Double
        return if (parsed != null && rate != null) parsed * rate else null
    }
}
