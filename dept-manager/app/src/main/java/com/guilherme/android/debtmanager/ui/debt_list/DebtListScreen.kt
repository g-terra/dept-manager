package com.guilherme.android.debtmanager.ui.debt_list

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guilherme.android.debtmanager.R
import com.guilherme.android.debtmanager.ui.misc.Dialog
import com.guilherme.android.debtmanager.util.UiEvent

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DebtListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: DebtListViewModel = hiltViewModel()
) {

    val debts = viewModel.debts.collectAsState(initial = emptyList())

    val scaffoldState = rememberScaffoldState()

    val showDeleteDialog = remember { mutableStateOf(false) }

    val showTotalsDialog = remember { mutableStateOf(false) }

    val selectId = remember { mutableStateOf(-1) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }

    Scaffold(

        scaffoldState = scaffoldState,
        floatingActionButton = {

            Row {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(DebtListEvent.OnCreateDebtClick)
                    },
                    backgroundColor = MaterialTheme.colors.primaryVariant

                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Debt",
                        tint = Color.White

                    )
                }

                Spacer(modifier = Modifier.padding(16.dp))

                FloatingActionButton(
                    onClick = {
                        showTotalsDialog.value = true
                    },
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calculate),
                        contentDescription = "Simulate",
                        tint = Color.White
                    )
                }
            }
        }
    ) {

        if (showDeleteDialog.value) {
            Dialog(
                onDismissRequest = { showDeleteDialog.value = false },
                onConfirm = {
                    showDeleteDialog.value = false
                    viewModel.onEvent(DebtListEvent.OnDeleteDebtConfirmation(selectId.value))
                },
                heading = "Delete",
                body = "Are you sure you want to delete this debt? This action cannot be undone."
            )
        }

        if (showTotalsDialog.value) {
            Dialog(
                onConfirm = {
                    showTotalsDialog.value = false
                },
                heading = "Total debts",
                body = "The sum of all debts is: ${debts.value.sumOf { it.amount }}",
                showCancel = false
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Debts",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(
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
                                            viewModel.onEvent(DebtListEvent.OnDebtClick(debt))
                                        },
                                        onLongPress = {
                                            println("DebtListScreen: onLongPress: $debt")
                                            showDeleteDialog.value = true
                                            selectId.value = debt.id!!
                                        }
                                    )
                                }
                                .padding(3.dp)
                        )
                    }
                }
            }

        }
    }
}


