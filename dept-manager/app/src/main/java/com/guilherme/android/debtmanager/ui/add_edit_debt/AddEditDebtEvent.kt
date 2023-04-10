package com.guilherme.android.debtmanager.ui.add_edit_debt

sealed class AddEditDebtEvent {
    data class DebtorNameChanged(val name: String) : AddEditDebtEvent()
    data class AmountChanged(val amount: String) : AddEditDebtEvent()
    object SaveDebtClicked : AddEditDebtEvent()
    object CloseTotalDebt : AddEditDebtEvent()
    object SimulateRepaymentClicked : AddEditDebtEvent()
    object UndoChangesClicked : AddEditDebtEvent()

}