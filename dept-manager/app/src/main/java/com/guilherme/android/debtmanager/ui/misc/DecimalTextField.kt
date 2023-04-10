package com.guilherme.android.debtmanager.ui.misc

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.text.DecimalFormat

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DecimalTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    initialValue: String = "0.00",
    format: DecimalFormat = remember { DecimalFormat("0.00") },
    maxInputLength: Int = 9,
    label: String = "value",
    onDone: () -> Unit = {}

) {
    val textFieldValue = remember(value, initialValue) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length, value.length)))
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue.value,
        onValueChange = { newInput ->
            if (newInput.text.length <= maxInputLength || newInput.text.length < textFieldValue.value.text.length) {
                val newValue = handleCurrencyInput(newInput.text, textFieldValue.value.text, format)
                textFieldValue.value = newValue
                onValueChange(newValue.text)
            }
        },
        label = {
            Text(
                label,
                color = MaterialTheme.colors.primaryVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            onDone()
        }),
        // Add any other styling or configuration you need for your TextField
    )
}


private fun handleCurrencyInput(
    newInput: String,
    oldInput: String,
    format: DecimalFormat
): TextFieldValue {
    val isBackspace = newInput.length < oldInput.length

    val onlyDigits = if (isBackspace) {
        val oldDigits = oldInput.filter { it.isDigit() }
        oldDigits.dropLast(1)
    } else {
        newInput.filter { it.isDigit() }
    }

    if (onlyDigits.isEmpty()) {
        return TextFieldValue(text = "0.00", selection = TextRange(4, 4))
    }

    val inputAsCents = onlyDigits.toLongOrNull() ?: 0L
    val inputAsCurrency = inputAsCents / 100.0
    val formattedText = format.format(inputAsCurrency)
    val textLength = formattedText.length
    return TextFieldValue(text = formattedText, selection = TextRange(textLength, textLength))
}

