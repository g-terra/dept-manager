package com.guilherme.android.debtmanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Debt::class], version = 3)
abstract class DebtDatabase : RoomDatabase() {
    abstract val dao: DebtDao
}