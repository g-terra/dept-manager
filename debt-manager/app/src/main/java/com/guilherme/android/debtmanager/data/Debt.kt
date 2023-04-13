package com.guilherme.android.debtmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Debt(

    val debtorName: String,

    val amount: Double,

    @PrimaryKey
    val id: Int? = null
)
