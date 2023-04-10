package com.guilherme.android.debtmanager.ui.simulation

sealed class SimulationEvent {

    data class InterestRateChanged(val interestRate: Float) : SimulationEvent()

    data class RepaymentRateChanged(val repaymentRate: Float) : SimulationEvent()

    object StartSimulationClicked : SimulationEvent()

    object StopSimulationClicked : SimulationEvent()

    object BackButtonClicked : SimulationEvent()
}