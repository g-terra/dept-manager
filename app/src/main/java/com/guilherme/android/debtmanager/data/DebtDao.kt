package com.guilherme.android.debtmanager.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt)

    @Delete
    suspend fun deleteDebt(debt: Debt)

    @Query("SELECT * FROM debt WHERE id = :id")
    suspend fun getDebtById(id: Int): Debt?

    @Query("SELECT * FROM debt")
    fun getDebts(): Flow<List<Debt>>

}