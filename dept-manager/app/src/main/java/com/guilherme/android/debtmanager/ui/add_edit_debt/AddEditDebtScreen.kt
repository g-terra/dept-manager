package com.guilherme.android.debtmanager.ui.add_edit_debt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guilherme.android.debtmanager.ui.misc.Dialog
import com.guilherme.android.debtmanager.ui.misc.DecimalTextField
import com.guilherme.android.debtmanager.util.UiEvent

@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter",
    "UnrememberedMutableState",
    "RememberReturnType"
)
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
        viewModel.uiEvent.collect { event ->
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {


                if (viewModel.debt != null) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.onEvent(AddEditDebtEvent.OnSendNotificationClick)
                        },
                        backgroundColor = MaterialTheme.colors.primaryVariant,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notify",
                            tint = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                    FloatingActionButton(
                        onClick = {
                            viewModel.onEvent(AddEditDebtEvent.OnSimulateRepaymentClick)
                        },
                        backgroundColor = MaterialTheme.colors.primaryVariant
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Simulate repayment",
                            tint = Color.White

                        )
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                }
                FloatingActionButton(
                    onClick = {
                        if (viewModel.hasChanged) {
                            showChangesDialog.value = true
                        } else {
                            viewModel.onEvent(AddEditDebtEvent.OnSaveDebtClick)
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
        },
    ) {

        if (showChangesDialog.value) {
            Dialog(
                confirmationText = "Save changes",
                cancelText = "Undo changes",
                onConfirm = {
                    viewModel.onEvent(AddEditDebtEvent.OnSaveDebtClick)
                },
                onDismissRequest = {
                    showChangesDialog.value = false
                    viewModel.onEvent(AddEditDebtEvent.OnUndoChangesClick)
                },
                heading = "Edit",
                body = "Are you sure you want to edit this debt?"
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primaryVariant),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (viewModel.debt != null) "Edit Debt" else "Add Debt",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = viewModel.debtorName,
                    onValueChange = {
                        viewModel.onEvent(AddEditDebtEvent.OnDebtorNameChange(it))
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
                    onValueChange = { viewModel.onEvent(AddEditDebtEvent.OnAmountChange(it)) },
                )

            }
        }
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

