package com.guilherme.android.debtmanager.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DebtContentProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.guilherme.android.debtmanager.data.DebtContentProvider"
        private const val DEBTS_TABLE = "debts"
        private const val DEBTS = 1
        private const val DEBTS_ID = 2
    }


   @Inject lateinit var debtRepository: DebtRepository

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, DEBTS_TABLE, DEBTS)
        addURI(AUTHORITY, "$DEBTS_TABLE/#", DEBTS_ID)
    }

    override fun onCreate(): Boolean {

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("_id", "name", "amount"))

        when (uriMatcher.match(uri)) {
            DEBTS -> {
                runBlocking {
                    debtRepository.getDebts().first().forEach { debt ->
                        cursor.addRow(arrayOf(debt.id, debt.debtorName, debt.amount))
                    }
                }
            }
            DEBTS_ID -> {
                val debtId = ContentUris.parseId(uri)
                runBlocking {
                    debtRepository.getDebtById(debtId.toInt())?.let { debt ->
                        cursor.addRow(arrayOf(debt.id, debt.debtorName, debt.amount))
                    }
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        return cursor
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Not implemented
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Not implemented
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            DEBTS -> "vnd.android.cursor.dir/$AUTHORITY.$DEBTS_TABLE"
            DEBTS_ID -> "vnd.android.cursor.item/$AUTHORITY.$DEBTS_TABLE"
            else -> null
        }
    }
}
