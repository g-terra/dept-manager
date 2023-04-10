package com.guilherme.android.debtmanager.ui.debt_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.android.debtmanager.data.DebtRepository
import com.guilherme.android.debtmanager.util.Routes
import com.guilherme.android.debtmanager.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebtListViewModel @Inject constructor(
    private val debtRepository: DebtRepository
) : ViewModel() {

    val debts = debtRepository.getDebts()

    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: DebtListEvent) {
        viewModelScope.launch{
            when (event) {
                is DebtListEvent.OnDebtClick -> {
                    println("Debt clicked: ${event.debt.id}")
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_DEBT + "?debtId=${event.debt.id}"))
                    return@launch
                }

                is DebtListEvent.OnCreateDebtClick -> {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_DEBT))
                    return@launch
                }

                is DebtListEvent.OnSimulationClick -> {
                }

                is DebtListEvent.OnDeleteDebtConfirmation -> {
                    println("Debt to be deleted: ${event.debtId}")
                    val toBeDeleted = debtRepository.getDebtById(event.debtId)
                    debtRepository.deleteDebt(toBeDeleted!!)
                    return@launch
                }

            }
        }
    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


}
