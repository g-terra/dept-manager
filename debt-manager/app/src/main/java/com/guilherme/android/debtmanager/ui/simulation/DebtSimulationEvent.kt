package com.guilherme.android.debtmanager.ui.simulation

sealed class DebtSimulationEvent {

    data class InterestRateChanged(val interestRate: Float) : DebtSimulationEvent()

    data class RepaymentRateChanged(val repaymentRate: Float) : DebtSimulationEvent()

    object StartDebtSimulationClicked : DebtSimulationEvent()

    object StopDebtSimulationClicked : DebtSimulationEvent()

    object BackButtonClicked : DebtSimulationEvent()
}