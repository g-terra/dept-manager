package com.guilherme.android.debtmanager.data

import kotlinx.coroutines.flow.Flow

class DeptRepositoryImpl(
    private val dao: DebtDao
) : DebtRepository {
    override suspend fun insertDebt(debt: Debt) {
        dao.insertDebt(debt)
    }

    override suspend fun deleteDebt(debt: Debt) {
        dao.deleteDebt(debt)
    }

    override suspend fun getDebtById(id: Int): Debt? {
        return dao.getDebtById(id)
    }

    override fun getDebts(): Flow<List<Debt>> {
        return dao.getDebts()
    }
}