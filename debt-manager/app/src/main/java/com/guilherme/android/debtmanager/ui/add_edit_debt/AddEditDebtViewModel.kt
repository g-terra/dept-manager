package com.guilherme.android.debtmanager.ui.add_edit_debt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilherme.android.debtmanager.data.Debt
import com.guilherme.android.debtmanager.data.DebtRepository
import com.guilherme.android.debtmanager.util.Routes
import com.guilherme.android.debtmanager.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var debt by mutableStateOf<Debt?>(null)
        private set

    var debtorName by mutableStateOf("")
        private set

    var amount by mutableStateOf("0.00")
        private set

    var hasChanged by mutableStateOf(false)
        private set

    private val _shareTextFlow = MutableSharedFlow<String>()
    val shareTextFlow: SharedFlow<String> = _shareTextFlow

    private val _uiEvent = Channel<UiEvent>()
    val uiEventFlow = _uiEvent.receiveAsFlow()

    init {
        val debtId = savedStateHandle.get<Int>("debtId") ?: -1
        if (debtId != -1) {
            viewModelScope.launch {
                debtRepository.getDebtById(debtId)?.let { debt ->
                    debtorName = debt.debtorName
                    amount = debt.amount.toString()
                    this@AddEditDebtViewModel.debt = debt
                }
            }
        }
    }

    fun onEvent(event: AddEditDebtEvent) {
        when (event) {
            is AddEditDebtEvent.DebtorNameChanged -> handleDebtorNameChange(event)
            is AddEditDebtEvent.AmountChanged -> handleAmountChange(event)
            is AddEditDebtEvent.SaveDebtClicked -> handleSaveDebt()
            is AddEditDebtEvent.CloseTotalDebt -> handleSendNotification()
            is AddEditDebtEvent.SimulateRepaymentClicked -> handleSimulateRepayment()
            is AddEditDebtEvent.UndoChangesClicked -> handleUndoChanges()
        }
    }

    private fun handleSendNotification() {
        onShareText(
            """
            Hey $debtorName! 
            You owe me $amount Zloty.
            Please pay me back as soon as possible. 
            Thanks!
        """.trimIndent()
        )
    }

    private fun handleAmountChange(event: AddEditDebtEvent.AmountChanged) {
        amount = event.amount
        hasChanged = true
    }

    private fun handleDebtorNameChange(event: AddEditDebtEvent.DebtorNameChanged) {
        debtorName = event.name
        hasChanged = true
    }

    private fun handleSaveDebt() {
        viewModelScope.launch {
            val validateError = getFieldValidationError()

            if (validateError.isNotEmpty()) {
                sendUiEvent(UiEvent.ShowSnackbar(validateError))
                return@launch
            }

            debtRepository.insertDebt(
                Debt(
                    debtorName = debtorName,
                    amount = amount.toDouble(),
                    id = debt?.id
                )
            )

            hasChanged = false
            sendUiEvent(UiEvent.PopBackStack)
        }
    }

    private fun getFieldValidationError(): String {
        if (debtorName.isEmpty()) {
            return "Name cannot be empty"
        }
        return ""
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun onShareText(text: String) {
        viewModelScope.launch {
            _shareTextFlow.emit(text)
        }
    }

    private fun handleSimulateRepayment() {
        sendUiEvent(UiEvent.Navigate(Routes.SIMULATION + "?debtId=${debt?.id}"))
    }

    private fun handleUndoChanges() {
        amount = debt?.amount.toString()
        debtorName = debt?.debtorName ?: ""
        hasChanged = false
    }

}