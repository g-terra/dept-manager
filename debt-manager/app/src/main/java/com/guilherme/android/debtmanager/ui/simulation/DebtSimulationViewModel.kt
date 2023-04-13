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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtSimulationViewModel @Inject constructor(
    debtRepository: DebtRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _uiEvent = Channel<UiEvent>()
    val uiEventFlow = _uiEvent.receiveAsFlow()

    var debtSimulation by mutableStateOf<DebtSimulation>(
        DebtSimulation(Debt("", 0.0), 0f, 0f))
                private set

        var debt by mutableStateOf < Debt ? > (Debt("", 0.0))
        private set

    private var simulationJob: Job? = null


    init {
        savedStateHandle.get<Int>("debtId")?.let { debtId ->
            viewModelScope.launch {
                debtRepository.getDebtById(debtId)?.let { fetchedDebt ->
                    debt = fetchedDebt
                    debtSimulation =
                        DebtSimulation(debt = fetchedDebt, interestRate = 0f, repaymentRate = 0f)
                    resetSimulation()
                }
            }
        }
    }


    fun onEvent(event: DebtSimulationEvent) {
        when (event) {
            is DebtSimulationEvent.InterestRateChanged -> {
                resetSimulation()
                debtSimulation.interestRate = event.interestRate
            }
            is DebtSimulationEvent.RepaymentRateChanged -> {
                resetSimulation()
                debtSimulation.repaymentRate = event.repaymentRate
            }
            is DebtSimulationEvent.StartDebtSimulationClicked -> {
                resetSimulation()
                startSimulation()
            }
            is DebtSimulationEvent.StopDebtSimulationClicked -> stopSimulation()
            is DebtSimulationEvent.BackButtonClicked -> sendUiEvent(UiEvent.PopBackStack)
        }
    }

    private fun startSimulation() {
        if (!debtSimulation.isSimulationRunning) {
            simulationJob = viewModelScope.launch {
                debtSimulation.startSimulation()
            }
        }
    }

    private fun stopSimulation() {
        debtSimulation.stopSimulation()
        simulationJob?.cancel()
        simulationJob = null
    }

    private fun resetSimulation() {
        debtSimulation.resetSimulation()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}

