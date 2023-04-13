package com.guilherme.android.debtmanager.ui.simulation


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.guilherme.android.debtmanager.data.Debt
import kotlinx.coroutines.delay

class DebtSimulation(
    private val debt: Debt,
    var interestRate: Float,
    var repaymentRate: Float
) {

    var isSimulationRunning by mutableStateOf(false)
        private set

    var timeElapsed by mutableStateOf(0)
        private set

    var remainingAmount by mutableStateOf(debt.amount)
        private set

    var totalInterest by mutableStateOf(0.0)
        private set

    var simulationHistory by mutableStateOf(emptyList<DebtSimulationEntry>())
        private set

    suspend fun startSimulation() {

        print("startSimulation: remainingAmount: $remainingAmount")

        isSimulationRunning = true

        var elapsedSeconds = 1L

        while (remainingAmount > 0 && isSimulationRunning) {
            val actualPaymentRate = if (remainingAmount <= repaymentRate) {
                remainingAmount
            } else {
                repaymentRate.toDouble()
            }

            remainingAmount -= actualPaymentRate

            val decimalInterest = interestRate / 100

            totalInterest += remainingAmount * decimalInterest
            remainingAmount *= 1 + decimalInterest

            if (remainingAmount < 0) {
                remainingAmount = 0.0
            }

            timeElapsed = elapsedSeconds.toInt()

            delay(1000)
            elapsedSeconds++

            pushSimulationHistory(
                timeElapsed = timeElapsed,
                payment = actualPaymentRate,
                remainingAmount = remainingAmount
            )
        }

        isSimulationRunning = false
    }

    fun stopSimulation() {
        isSimulationRunning = false
    }

    fun resetSimulation() {
        stopSimulation()
        totalInterest = 0.0
        simulationHistory = listOf()
        remainingAmount = debt.amount
        timeElapsed = 0
    }

    private fun pushSimulationHistory(
        timeElapsed: Int,
        remainingAmount: Double,
        payment: Double,
    ) {

        println("pushSimulationHistory: timeElapsed: $timeElapsed, remainingAmount: $remainingAmount, payment: $payment")

        simulationHistory = simulationHistory + DebtSimulationEntry(
            index = timeElapsed,
            payment = payment,
            remaining = remainingAmount
        )
    }
}
