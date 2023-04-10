package com.guilherme.android.debtmanager.ui.add_edit_debt

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guilherme.android.debtmanager.R
import com.guilherme.android.debtmanager.ui.misc.Dialog
import com.guilherme.android.debtmanager.ui.misc.DecimalTextField
import com.guilherme.android.debtmanager.ui.misc.ScreenHeader
import com.guilherme.android.debtmanager.util.UiEvent


@Composable
fun AddEditDebtScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditDebtViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val startActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { }

    val showChangesDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.shareTextFlow.collect { text ->
            shareText(text, startActivityLauncher)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEventFlow.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            DebtAddEditFab(viewModel, showChangesDialog)
        },
        content = { paddingValues ->
            DebtAddEditContainer(paddingValues, showChangesDialog, viewModel)
        }
    )
}

@Composable
private fun DebtAddEditContainer(
    paddingValues: PaddingValues,
    showChangesDialog: MutableState<Boolean>,
    viewModel: AddEditDebtViewModel
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        if (showChangesDialog.value) {
            SaveDebtChangeDialog(viewModel, showChangesDialog)
        }
        DebtFormContent(viewModel)
    }
}

@Composable
private fun DebtFormContent(viewModel: AddEditDebtViewModel) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ScreenHeader(
            modifier = Modifier,
            title = stringResource(R.string.debts),
        )

        Spacer(modifier = Modifier.padding(16.dp))

        DebFormFields(viewModel)
    }
}

@Composable
private fun DebFormFields(viewModel: AddEditDebtViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = viewModel.debtorName,
            onValueChange = {
                viewModel.onEvent(AddEditDebtEvent.DebtorNameChanged(it))
            },

            label = {
                Text(
                    text = "Debtor Name",
                    color = MaterialTheme.colors.primaryVariant
                )
            },
            placeholder = {
                Text(text = "e.g John Doe")
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.padding(8.dp))

        DecimalTextField(
            value = viewModel.amount,
            initialValue = viewModel.amount,
            onValueChange = { viewModel.onEvent(AddEditDebtEvent.AmountChanged(it)) },
        )
    }
}

@Composable
private fun SaveDebtChangeDialog(
    viewModel: AddEditDebtViewModel,
    showChangesDialog: MutableState<Boolean>
) {
    Dialog(
        confirmationText = "Save changes",
        cancelText = "Undo changes",
        onConfirm = {
            viewModel.onEvent(AddEditDebtEvent.SaveDebtClicked)
        },
        onDismissRequest = {
            showChangesDialog.value = false
            viewModel.onEvent(AddEditDebtEvent.UndoChangesClicked)
        },
        heading = "Edit",
        body = "Are you sure you want to edit this debt?"
    )
}

@Composable
private fun DebtAddEditFab(
    viewModel: AddEditDebtViewModel,
    showChangesDialog: MutableState<Boolean>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        if (viewModel.debt != null) {
            DebtNotifyFab(viewModel)
            Spacer(modifier = Modifier.padding(16.dp))

            DebtSimulationFab(viewModel)
            Spacer(modifier = Modifier.padding(16.dp))
        }

        DebtSaveFab(viewModel, showChangesDialog)
    }
}

@Composable
private fun DebtSimulationFab(viewModel: AddEditDebtViewModel) {
    FloatingActionButton(
        onClick = {
            viewModel.onEvent(AddEditDebtEvent.SimulateRepaymentClicked)
        },
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Simulate repayment",
            tint = Color.White

        )
    }
}

@Composable
private fun DebtNotifyFab(viewModel: AddEditDebtViewModel) {
    FloatingActionButton(
        onClick = {
            viewModel.onEvent(AddEditDebtEvent.CloseTotalDebt)
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notify",
            tint = Color.White,
        )
    }
}

@Composable
private fun DebtSaveFab(
    viewModel: AddEditDebtViewModel,
    showChangesDialog: MutableState<Boolean>
) {
    FloatingActionButton(
        onClick = {
            if (viewModel.debt != null && viewModel.hasChanged) {
                showChangesDialog.value = true
            } else {
                viewModel.onEvent(AddEditDebtEvent.SaveDebtClicked)
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Save",
            tint = Color.White
        )
    }
}


fun shareText(text: String, launcher: ActivityResultLauncher<Intent>) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    launcher.launch(shareIntent)
}

