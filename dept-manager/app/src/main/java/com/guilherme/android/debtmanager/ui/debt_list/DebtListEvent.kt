package com.guilherme.android.debtmanager.ui.debt_list

import com.guilherme.android.debtmanager.data.Debt

sealed class DebtListEvent {

    object CreateDebtClicked : DebtListEvent()
    object CalculateTotalDebtClick : DebtListEvent()
    data class DebtClicked(val debt: Debt) : DebtListEvent()
    data class DebtDeletionRequested(val debtId: Int) : DebtListEvent()
    object DebtDeletionConfirmed : DebtListEvent()
    object DebtDeletionCancelled : DebtListEvent()
    object CloseTotalDebtDialogClicked : DebtListEvent()

}