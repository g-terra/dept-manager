package com.guilherme.android.debtmanager.ui.misc

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Slider
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.guilherme.android.debtmanager.R
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.log10

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SliderField(
    modifier: Modifier = Modifier,
    initialValue: Float = 0.00f,
    min: Float = 0.00f,
    max: Float = 100.00f,
    onValueChange: (Float) -> Unit = {},
    numberFormat: String = "0.00"
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val formatting = DecimalFormat(numberFormat)

    val currentValue = remember { mutableStateOf(initialValue) }
    val textFieldValue = remember { mutableStateOf(formatting.format(initialValue)) }

    val numberOfDigits = max.toString().length + 1
    val digitWidth = 14.dp // You can adjust this value based on your desired width scaling factor
    val textFieldWidth = numberOfDigits * digitWidth


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Slider(
            value = currentValue.value,
            onValueChange = {
                val clampedValue = it.coerceIn(min, max)
                currentValue.value = clampedValue
                textFieldValue.value = formatting.format(clampedValue)
                onValueChange(clampedValue)
            },
            valueRange = min..max,
            modifier = Modifier
                .weight(.6f)
        )

        TextField(
            modifier = Modifier
                .width(textFieldWidth) // Set the width based on the calculated textFieldWidth
                .padding(2.dp),
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it
                if (it.isEmpty()) {
                    onValueChange(0.00f)
                } else {
                    try {
                        val floatValue = it.toFloat().coerceIn(min, max)
                        onValueChange(floatValue)
                        currentValue.value = floatValue
                    } catch (e: NumberFormatException) {
                        handleNumberException(e)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    try {
                        val floatValue = textFieldValue.value.toFloat().coerceIn(min, max)
                        onValueChange(floatValue)
                        currentValue.value = floatValue
                        textFieldValue.value = formatting.format(floatValue)
                    } catch (e: NumberFormatException) {
                        handleNumberException(e)
                    } finally {
                        keyboardController?.hide()
                    }
                }
            )
        )
    }
}

private fun handleNumberException(e: NumberFormatException) {
    println("NumberFormatException: ${e.message}")
}


@Preview(showBackground = true)
@Composable
fun SliderFieldPreview() {
    SliderField()
}