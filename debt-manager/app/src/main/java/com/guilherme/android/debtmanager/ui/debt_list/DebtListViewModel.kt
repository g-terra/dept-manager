package com.guilherme.android.debtmanager.ui.debt_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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



    private val _uiEventChannel = Channel<UiEvent>()
    val uiEventFlow = _uiEventChannel.receiveAsFlow()

    var showTotalDebtDialog by mutableStateOf(false)
        private set

    var showDebtDeleteDialog by mutableStateOf(false)
        private set

    var selectedId by mutableStateOf(-1)
        private set

    val debts = debtRepository.getDebts()

    fun onEvent(event: DebtListEvent) {
        viewModelScope.launch {
            when (event) {
                is DebtListEvent.DebtClicked -> {
                    println("Debt clicked: ${event.debt.id}")
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_DEBT + "?debtId=${event.debt.id}"))
                }
                is DebtListEvent.CreateDebtClicked -> {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_DEBT))
                }
                is DebtListEvent.DebtDeletionConfirmed -> {
                    deleteDebt(selectedId)
                    resetDeleteDialogState()
                }
                is DebtListEvent.DebtDeletionRequested -> {
                    showDebtDeleteDialog = true
                    selectedId = event.debtId
                }
                is DebtListEvent.DebtDeletionCancelled -> {
                    resetDeleteDialogState()
                }
                is DebtListEvent.CloseTotalDebtDialogClicked -> {
                    showTotalDebtDialog = false
                }
                is DebtListEvent.CalculateTotalDebtClick -> {
                    showTotalDebtDialog = true
                }
            }
        }
    }

    private fun resetDeleteDialogState() {
        showDebtDeleteDialog = false
        selectedId = -1
    }

    private suspend fun deleteDebt(debtId : Int) {
        println("Debt to be deleted: $debtId")
        val toBeDeleted = debtRepository.getDebtById(debtId)
        if (toBeDeleted != null) {
            debtRepository.deleteDebt(toBeDeleted)
        } else {
            sendUiEvent(UiEvent.ShowSnackbar("Debt not found", "Ok"))
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEventChannel.send(event)
        }
    }
}
