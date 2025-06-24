package it.unibo.composeui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.composeui.viewmodel.WalletViewModelFactory
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import it.unibo.domain.model.WalletEntry

@Composable
fun WalletScreen(
    currencyRepository: CurrencyRepository,
    walletRepository: WalletRepository
) {
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(currencyRepository, walletRepository)
    )

    val entries by viewModel.entries.collectAsState()
    val total by viewModel.total.collectAsState()
    val currencies by viewModel.currencies.collectAsStateWithLifecycle(emptyList())

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )

    var showAddDialog by remember { mutableStateOf(false) }
    var editEntryId by remember { mutableStateOf<Int?>(null) }
    var deleteEntryId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrencies()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Wallet")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Total Value: ${"%.2f".format(total)}€",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    WalletDonutChart(
                        entries = entries,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 16.dp)
                    )
                }
            }

            itemsIndexed(entries) { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors[index % colors.size], shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${item.currency}: %.2f".format(item.amount))
                }
            }

            item {
                if (entries.isEmpty()) {
                    Text("No wallets available.", color = MaterialTheme.colorScheme.onBackground)
                } else {
                    Text("Wallets", style = MaterialTheme.typography.titleLarge)
                }
            }

            items(entries) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.currency, style = MaterialTheme.typography.bodyMedium)
                        Text("%.2f".format(item.amount), style = MaterialTheme.typography.bodyMedium)
                        Row {
                            IconButton(onClick = { editEntryId = item.id }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { deleteEntryId = item.id }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

        if (showAddDialog) {
        AddWalletDialog(
            availableCurrencies = currencies.filter { currency ->
                entries.none { it.currency == currency }
            },
            onDismiss = { showAddDialog = false },
            onConfirm = { currency, amount ->
                viewModel.addWallet(currency, amount)
                showAddDialog = false
            }
        )
    }

    val entryToEdit = entries.find { it.id == editEntryId }
    if (entryToEdit != null) {
        EditWalletDialog(
            initialAmount = entryToEdit.amount,
            onDismiss = { editEntryId = null },
            onConfirm = { delta ->
                viewModel.modifyWallet(entryToEdit, delta)
                editEntryId = null
            }
        )
    }

    val entryToDelete = entries.find { it.id == deleteEntryId }
    if (entryToDelete != null) {
        ConfirmDeleteDialog(
            onConfirm = {
                viewModel.deleteWallet(entryToDelete)
                deleteEntryId = null
            },
            onDismiss = { deleteEntryId = null }
        )
    }
}

@Composable
fun AddWalletDialog(
    availableCurrencies: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var selectedCurrency by remember { mutableStateOf(availableCurrencies.firstOrNull() ?: "") }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Wallet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DropdownMenuCurrencySelector(
                    currencies = availableCurrencies,
                    selected = selectedCurrency,
                    onSelected = { selectedCurrency = it }
                )
                TextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && selectedCurrency.isNotBlank()) {
                        onConfirm(selectedCurrency, amount)
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditWalletDialog(
    initialAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Amount") },
        text = {
            TextField(
                value = amountText,
                onValueChange = { if (it.all { ch -> ch.isDigit() || ch == '.' || ch == '-' }) amountText = it },
                label = { Text("Delta (+/-)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val delta = amountText.toDoubleOrNull()
                    if (delta != null) {
                        onConfirm(delta)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Wallet") },
        text = { Text("Are you sure you want to delete this wallet?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuCurrencySelector(
    currencies: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WalletDonutChart(
    entries: List<WalletEntry>,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    thickness: Dp = 30.dp
) {
    val total = entries.sumOf { it.amount }
    if (total == 0.0) return

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )

    Canvas(modifier = modifier.size(size)) {
        var startAngle = -90f
        entries.forEachIndexed { index, entry ->
            val sweep = (entry.amount / total * 360f).toFloat()
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}
