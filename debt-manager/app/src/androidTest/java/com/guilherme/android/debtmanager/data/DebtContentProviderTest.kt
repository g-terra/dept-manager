package com.guilherme.android.debtmanager.data

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DebtContentProviderTest {

    var mockDebtRepository: DebtRepository = mock()

    @Test
    fun should_return_list_of_debts() = runBlockingTest {
        val debts = listOf(
            Debt("Test1", 101.0, 1),
            Debt("Test2", 102.0, 2),
            Debt("Test3", 103.0, 3)
        )
        whenever(mockDebtRepository.getDebts()).thenReturn(flowOf(debts))

        val contentProvider = DebtContentProvider().apply {
            attachInfo(InstrumentationRegistry.getInstrumentation().targetContext, null)
            debtRepository = mockDebtRepository
        }

        val cursor = contentProvider.query(
            Uri.parse("content://com.guilherme.android.debtmanager.data.DebtContentProvider/debts"),
            null,
            null,
            null,
            null
        )

        assertEquals(3, cursor.count)
        cursor.moveToFirst()
        assertEquals(1, cursor.getInt(0))
        cursor.moveToNext()
        assertEquals(2, cursor.getInt(0))
        cursor.moveToNext()
        assertEquals(3, cursor.getInt(0))

    }


    @Test
    fun should_return_debt_for_given_id() = runBlockingTest {

        whenever(mockDebtRepository.getDebtById(1)).thenReturn(Debt("Test1", 101.0, 1))
        whenever(mockDebtRepository.getDebtById(2)).thenReturn(Debt("Test2", 101.0, 2))
        whenever(mockDebtRepository.getDebtById(3)).thenReturn(Debt("Test3", 101.0, 3))

        val contentProvider = DebtContentProvider().apply {
            attachInfo(InstrumentationRegistry.getInstrumentation().targetContext, null)
            debtRepository = mockDebtRepository
        }

        val cursor = contentProvider.query(
            Uri.parse("content://com.guilherme.android.debtmanager.data.DebtContentProvider/debts/2"),
            null,
            null,
            null,
            null
        )

        assertEquals(1, cursor.count)

        cursor.moveToFirst()
        assertEquals(2, cursor.getInt(0))
    }

}






