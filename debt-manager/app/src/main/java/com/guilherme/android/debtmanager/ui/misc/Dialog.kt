package com.guilherme.android.debtmanager.ui.misc

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Dialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
    heading : String,
    body : String,
    confirmationText : String = "Confirm",
    cancelText : String = "Cancel",
    showCancel : Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(heading) },
        text = { Text(body) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                )
            ) {
                Text(
                    text = confirmationText,
                )
            }
        },
        dismissButton = showCancel.let {
            if (it) {
                {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.secondary,
                        )
                    ) {
                        Text(
                            text = cancelText,
                        )
                    }
                }
            } else {
                null
            }
        },
    )
}

@Preview
@Composable
fun DialogPreview() {
    Dialog(
        onDismissRequest = {},
        onConfirm = {},
        heading = "Heading",
        body = "Body",
        confirmationText = "Confirm",
        cancelText = "Cancel",
    )
}