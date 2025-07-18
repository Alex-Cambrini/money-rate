package it.unibo.composeui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.unibo.composeui.theme.Primary


@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun CurrencyInputRowPreview() {
    CurrencyInputRow(
        currency = "EUR",
        onCurrencyChange = {},
        amount = TextFieldValue("100"),
        onAmountChange = {},
        currencies = listOf("EUR" to "Euro", "USD" to "Dollar")
    )
}

@Composable
fun CurrencyInputRow(
    currency: String,
    onCurrencyChange: (String) -> Unit,
    amount: TextFieldValue,
    onAmountChange: (TextFieldValue) -> Unit,
    currencies: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable { expanded = true }
        ) {
            val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currency,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .rotate(rotation)
                        .align(Alignment.CenterVertically)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text("$code – $name") },
                        onClick = {
                            onCurrencyChange(code)
                            expanded = false
                        }
                    )
                }
            }
        }

        val isDark = androidx.compose.foundation.isSystemInDarkTheme()
        val textColor = if (isDark) Color.White else Color.Black
        var isFocused by remember { mutableStateOf(false) }

        BasicTextField(
            value = amount,
            onValueChange = onAmountChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.End,
                color = textColor
            ),
            cursorBrush = SolidColor(Primary),
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState -> isFocused = focusState.isFocused },
            decorationBox = { innerTextField ->
                Box {
                    if (amount.text.isEmpty()) {
                        Text(
                            text = "e.g. 123.45",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
