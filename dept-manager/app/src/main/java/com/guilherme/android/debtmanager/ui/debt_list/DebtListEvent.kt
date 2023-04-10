package com.guilherme.android.debtmanager.ui.debt_list

import com.guilherme.android.debtmanager.data.Debt

sealed class DebtListEvent {

    object OnCreateDebtClick : DebtListEvent()

    object OnSimulationClick : DebtListEvent()

    data class OnDebtClick(val debt: Debt) : DebtListEvent()

    data class OnDeleteDebtConfirmation (val debtId: Int): DebtListEvent()
}