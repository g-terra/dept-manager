package com.guilherme.android.debtmanager.ui.add_edit_debt

sealed class AddEditDebtEvent {

    data class OnDebtorNameChange(val name: String) : AddEditDebtEvent()

    data class OnAmountChange(val amount: String) : AddEditDebtEvent()

    object OnSaveDebtClick : AddEditDebtEvent()

    object OnSendNotificationClick : AddEditDebtEvent()

    object OnSimulateRepaymentClick : AddEditDebtEvent()

    object OnUndoChangesClick : AddEditDebtEvent()


}