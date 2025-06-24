package it.unibo.composeui.screens

import androidx.compose.foundation.layout.*
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

    var showAddDialog by remember { mutableStateOf(false) }
    var editEntryId by remember { mutableStateOf<Int?>(null) }
    var deleteEntryId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrencies()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi Wallet")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Controvalore Totale: ${"%.2f".format(total)}€", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Text("Nessun wallet disponibile.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entries.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.currency}: ${item.amount}")
                            Row {
                                IconButton(onClick = { editEntryId = item.id }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Modifica")
                                }
                                IconButton(onClick = { deleteEntryId = item.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Elimina")
                                }
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
        title = { Text("Aggiungi Wallet") },
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
                    label = { Text("Quantità") },
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
                Text("Conferma")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
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
        title = { Text("Modifica Quantità") },
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
                Text("Salva")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
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
        title = { Text("Elimina Wallet") },
        text = { Text("Sei sicuro di voler eliminare questo wallet?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Elimina")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
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
            label = { Text("Valuta") },
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