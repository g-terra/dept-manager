package com.guilherme.android.debtmanager.ui.debt_list

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guilherme.android.debtmanager.R
import com.guilherme.android.debtmanager.data.Debt
import com.guilherme.android.debtmanager.ui.misc.Dialog
import com.guilherme.android.debtmanager.ui.misc.ScreenHeader
import com.guilherme.android.debtmanager.util.UiEvent
import java.text.DecimalFormat

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DebtListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: DebtListViewModel = hiltViewModel()
) {
    val debts = viewModel.debts.collectAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            DebtListFab(viewModel)
        }
    ) {
        if (viewModel.showDebtDeleteDialog) {
            ConfirmDeletionDialog(viewModel)
        }

        if (viewModel.showTotalDebtDialog) {
            ShowTotalsDialog(debts, viewModel)
        }

        DebtListContent(debts, viewModel)
    }
}

@Composable
private fun DebtListFab(viewModel: DebtListViewModel) {
    Row {
        FloatingActionButton(
            onClick = {
                viewModel.onEvent(DebtListEvent.CreateDebtClicked)
            },
            backgroundColor = MaterialTheme.colors.primaryVariant
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.new_debt),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))

        FloatingActionButton(
            onClick = {
                viewModel.onEvent(DebtListEvent.CalculateTotalDebtClick)
            },
            backgroundColor = MaterialTheme.colors.primaryVariant
        ) {
            Icon(
                painter = painterResource(R.drawable.calculate),
                contentDescription = stringResource(R.string.total_debts),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ConfirmDeletionDialog(
    viewModel: DebtListViewModel
) {
    Dialog(
        onDismissRequest = {
            viewModel.onEvent(DebtListEvent.DebtDeletionCancelled)
        },
        onConfirm = {
            viewModel.onEvent(DebtListEvent.DebtDeletionConfirmed)
        },
        heading = stringResource(R.string.delete),
        body = stringResource(R.string.delete_confirmation)
    )
}

@Composable
private fun ShowTotalsDialog(debts: State<List<Debt>>, viewModel: DebtListViewModel) {
    Dialog(
        onConfirm = {
            viewModel.onEvent(DebtListEvent.CloseTotalDebtDialogClicked)
        },
        confirmationText = stringResource(R.string.ok),
        heading = stringResource(id = R.string.total_debts),
        body = "${stringResource(R.string.sum_debts)} ${
            DecimalFormat(stringResource(R.string.decimal_format)).format(
                debts.value.sumOf { it.amount })
        }",
        showCancel = false
    )
}

@Composable
private fun DebtListContent(
    debts: State<List<Debt>>,
    viewModel: DebtListViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScreenHeader(
            modifier = Modifier,
            showLogo = true
        )
        DebtListContainer(debts, viewModel)
    }
}

@Composable
private fun DebtListContainer(
    debts: State<List<Debt>>,
    viewModel: DebtListViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 72.dp
                )
        ) {
            items(debts.value.size) { index ->
                val debt = debts.value[index]
                DebtItem(
                    debt = debt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(
                            Unit
                        ) {
                            detectTapGestures(
                                onTap = {
                                    println("DebtListScreen: onTap: $debt")
                                    viewModel.onEvent(DebtListEvent.DebtClicked(debt))
                                },
                                onLongPress = {
                                    println("DebtListScreen: onLongPress: $debt")
                                    viewModel.onEvent(DebtListEvent.DebtDeletionRequested(debtId = debt.id!!))
                                }
                            )
                        }
                        .padding(3.dp)
                )
            }
        }
    }
}
