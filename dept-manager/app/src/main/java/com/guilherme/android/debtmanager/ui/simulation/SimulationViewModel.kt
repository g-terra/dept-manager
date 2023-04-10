package com.guilherme.android.debtmanager.ui.simulation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.android.debtmanager.data.Debt
import com.guilherme.android.debtmanager.data.DebtRepository
import com.guilherme.android.debtmanager.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SimulationViewModel @Inject constructor(
    debtRepository: DebtRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var interestRate by mutableStateOf(0.0F)
        private set

    var repaymentRate by mutableStateOf(0.0F)
        private set

    var debt by mutableStateOf<Debt?>(Debt("", 0.0))
        private set

    var isSimulationRunning by mutableStateOf(false)
        private set

    var timeElapsed by mutableStateOf(0)
        private set

    var remainingAmount by mutableStateOf(0.0)
        private set

    var totalInterest by mutableStateOf(0.0)
        private set

    var simulationHistory by mutableStateOf(listOf<SimulationEntry>())
        private set


    private var simulationJob: Job? = null

    private val _uiEvent = Channel<UiEvent>()
    val uiEventFlow = _uiEvent.receiveAsFlow()

    init {
        val debtId = savedStateHandle.get<Int>("debtId") ?: -1

        if (debtId != -1) {
            viewModelScope.launch {

                println("fetching debt with debtId: $debtId")
                debtRepository.getDebtById(debtId)?.let { debt ->
                    this@SimulationViewModel.debt = debt
                    resetSimulation()
                }
            }
        }
    }

    fun onEvent(event: SimulationEvent) {
        when (event) {
            is SimulationEvent.InterestRateChanged -> {
                resetSimulation()
                interestRate = event.interestRate
            }
            is SimulationEvent.RepaymentRateChanged -> {
                resetSimulation()
                repaymentRate = event.repaymentRate
            }

            is SimulationEvent.StartSimulationClicked -> {
                resetSimulation()
                startSimulation()
            }

            is SimulationEvent.StopSimulationClicked -> {
                stopSimulation()
            }

            is SimulationEvent.BackButtonClicked -> {
                sendUiEvent(UiEvent.PopBackStack)
            }

        }
    }


    private fun startSimulation() {
        if (!isSimulationRunning && debt != null) {
            isSimulationRunning = true

            simulationJob = viewModelScope.launch {
                remainingAmount = debt!!.amount
                var elapsedSeconds = 1L

                while (remainingAmount > 0 && isSimulationRunning) {
                    val actualPaymentRate = if (remainingAmount <= repaymentRate) {
                        remainingAmount
                    } else {
                        repaymentRate
                    }

                    remainingAmount -= actualPaymentRate.toDouble()

                    //add interest to total interest
                    totalInterest += remainingAmount * (interestRate / 100) / TimeUnit.SECONDS.toSeconds(
                        1
                    )

                    remainingAmount *= 1 + (interestRate / 100) / TimeUnit.SECONDS.toSeconds(1)

                    if (remainingAmount < 0) {
                        remainingAmount = 0.0
                    }

                    timeElapsed = elapsedSeconds.toInt()

                    delay(1000)
                    elapsedSeconds++

                    pushSimulationHistory(
                        timeElapsed = timeElapsed,
                        payment = actualPaymentRate.toDouble(),
                        remainingAmount = remainingAmount
                    )
                }

                isSimulationRunning = false

                if (!isSimulationRunning) {
                    timeElapsed = 0
                }
            }
        }
    }


    private fun pushSimulationHistory(
        timeElapsed: Int,
        remainingAmount: Double,
        payment: Double,
    ) {

        var df = DecimalFormat("#.##")
        println("pushing simulation history with timeElapsed: $timeElapsed and remainingAmount: $remainingAmount")
        simulationHistory = simulationHistory + SimulationEntry(
            index = timeElapsed,
            payment = df.format(payment).toDouble(),
            remaining = df.format(remainingAmount).toDouble()
        )

    }


    private fun stopSimulation() {
        if (isSimulationRunning) {
            isSimulationRunning = false
            simulationJob?.cancel()
            simulationJob = null
        }
    }


    private fun resetSimulation() {
        stopSimulation()
        totalInterest = 0.0
        simulationHistory = listOf()
        remainingAmount = debt!!.amount
        timeElapsed = 0
    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}

