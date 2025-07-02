package it.unibo.domain.usecase.home

class CalculateConversionUseCase {
    operator fun invoke(amount: String, rate: Double?): Double? {
        val parsed = amount.toDoubleOrNull()
        return if (parsed != null && rate != null) parsed * rate else null
    }
}
