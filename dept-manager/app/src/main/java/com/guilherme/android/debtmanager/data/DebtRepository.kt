package com.guilherme.android.debtmanager.data

import kotlinx.coroutines.flow.Flow


interface DebtRepository {

    suspend fun insertDebt(debt: Debt)

    suspend fun deleteDebt(debt: Debt)

    suspend fun getDebtById(id: Int): Debt?

    fun getDebts(): Flow<List<Debt>>

}