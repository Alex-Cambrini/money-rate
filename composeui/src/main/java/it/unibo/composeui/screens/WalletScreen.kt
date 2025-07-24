package it.unibo.composeui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.unibo.composeui.R
import it.unibo.composeui.components.WalletEntryCard
import it.unibo.composeui.theme.Background
import it.unibo.composeui.theme.DarkBackground
import it.unibo.composeui.theme.DarkSurface
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.domain.model.WalletEntry
import java.util.Locale

/**
 * Schermata principale del portafoglio.
 * Mostra tutte le valute salvate, il loro valore in euro, un grafico a ciambella
 * e permette di aggiungere, modificare o eliminare voci.
 */
@Composable
fun WalletScreen(viewModel: WalletViewModel) {

    val combinedData by viewModel.combinedData.collectAsStateWithLifecycle()
    val entries = combinedData.entries
    val rates = combinedData.ratesCache

    val entriesInEuro by viewModel.entriesInEuro.collectAsStateWithLifecycle()

    val total by viewModel.total.collectAsState()
    val currencies: List<Pair<String, String>> by viewModel.currencies.collectAsStateWithLifecycle(
        emptyList()
    )

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
    val isDark = isSystemInDarkTheme()
    val cardBackground = if (isDark) DarkSurface else MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        println("DEBUG: WalletScreen LaunchedEffect(Unit) called.")
        viewModel.loadCurrencies()
        viewModel.loadRatesCache()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_wallet_description)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
        ) {
            item {
                Text(
                    text = String.format(
                        Locale.ITALY,
                        stringResource(R.string.total_value_format),
                        total
                    ),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(Dimens.sectionSpacing))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.sectionSpacing),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.donutChartSpacing)
                    ) {
                        if (entries.isNotEmpty()) {
                            if (rates.isNotEmpty()) {
                                WalletDonutChart(
                                    entries = entriesInEuro,
                                    modifier = Modifier.size(Dimens.donutChartSize),
                                    size = Dimens.donutChartSize,
                                    thickness = Dimens.donutChartThickness
                                )
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(Dimens.donutChartSize))
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
                        ) {
                            entries.forEachIndexed { index, item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(Dimens.legendBoxSize)
                                            .background(
                                                colors[index % colors.size],
                                                shape = CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.elementSpacing))
                                    Text(
                                        "${item.currencyCode}: ${
                                            String.format(
                                                Locale.ITALY,
                                                "%.2f",
                                                entriesInEuro[index].amount
                                            )
                                        } €"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (entries.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.no_wallets),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.wallets),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            items(entries) { item ->
                WalletEntryCard(
                    entry = item,
                    rates = rates,
                    onEdit = { editEntryId = it },
                    onDelete = { deleteEntryId = it },
                    viewModel = viewModel
                )
            }
        }
    }

    // Apertura dialog di aggiunta wallet
    if (showAddDialog) {
        AddWalletDialog(
            viewModel = viewModel,
            availableCurrencies = currencies.filter { (code, _) ->
                entries.none { it.currencyCode == code }
            },
            onDismiss = { showAddDialog = false },
            onConfirm = { currency, amount ->
                viewModel.addWallet(currency, amount)
                showAddDialog = false
            }
        )
    }

    // Gestione dialog di modifica wallet
    var triedSave by remember { mutableStateOf(false) }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val entryToEdit = entries.find { it.id == editEntryId }

    if (entryToEdit != null) {
        EditWalletDialog(
            viewModel = viewModel,
            errorMessage = errorMessage,
            onDismiss = {
                viewModel.clearError()
                triedSave = false
                editEntryId = null
            },
            onConfirm = { delta ->
                viewModel.modifyWallet(entryToEdit, delta)
                triedSave = true
            }
        )
    }

    if (triedSave && errorMessage == null) {
        viewModel.clearError()
        triedSave = false
        editEntryId = null
    }

    // Gestione dialog di conferma eliminazione
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

/**
 * Dialogo per aggiungere una nuova voce al portafoglio.
 * Permette di selezionare una valuta e inserire l'importo iniziale.
 */
@Composable
fun AddWalletDialog(
    viewModel: WalletViewModel,
    availableCurrencies: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    // Valuta selezionata inizialmente (prima disponibile oppure stringa vuota)
    var selectedCurrency by remember { mutableStateOf(availableCurrencies.firstOrNull()?.first.orEmpty()) }
    var amountText by remember { mutableStateOf("") }

    // Colori adattivi in base al tema
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_wallet), color = textColor) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)) {
                // Selettore a tendina delle valute
                DropdownMenuCurrencySelector(
                    currencies = availableCurrencies.map { "${it.first} - ${it.second}" },
                    selected = availableCurrencies.find { it.first == selectedCurrency }
                        ?.let { "${it.first} - ${it.second}" } ?: "",
                    onSelected = { selectedString ->
                        selectedCurrency = selectedString.substringBefore(" -")
                    }
                )
                // Campo di inserimento per l'importo (filtra solo numeri, punti, virgole)
                TextField(
                    value = amountText,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() || ch == '.' || ch == ',' }) amountText = it
                    },
                    label = { Text(stringResource(R.string.amount), color = textColor) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = textColor,
                        unfocusedLabelColor = textColor
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = viewModel.parseAmount(amountText)
                if (amount != null && selectedCurrency.isNotBlank()) {
                    onConfirm(selectedCurrency, amount)
                }
            }) {
                Text(stringResource(R.string.confirm), color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}

/**
 * Dialogo per modificare l'importo di una valuta esistente.
 * Accetta anche valori negativi per sottrarre dal totale.
 */
@Composable
fun EditWalletDialog(
    viewModel: WalletViewModel,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_amount), color = textColor) },
        text = {
            Column {
                // Campo input per il delta (può anche essere negativo)
                TextField(
                    value = amountText,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() || ch == '.' || ch == ',' || ch == '-' }) amountText =
                            it
                    },
                    label = { Text(stringResource(R.string.delta), color = textColor) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = backgroundColor,
                        unfocusedContainerColor = backgroundColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = textColor,
                        unfocusedLabelColor = textColor
                    )
                )
                // Mostra errore, se presente
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(Dimens.elementSpacing))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val delta = viewModel.parseDelta(amountText)
                if (delta != null) {
                    onConfirm(delta)
                }
            }) {
                Text(stringResource(R.string.save), color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}

/**
 * Dialogo di conferma per l’eliminazione di una voce dal portafoglio.
 * Richiede l’approvazione dell’utente prima di procedere.
 */
@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_wallet), color = textColor) },
        text = { Text(stringResource(R.string.confirm_delete_wallet), color = textColor) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete_description), color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}

/**
 * Selettore a tendina che mostra l’elenco delle valute disponibili.
 * Viene usato all’interno dei dialoghi per selezionare la valuta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuCurrencySelector(
    currencies: List<String>, // Formato: "EUR - Euro"
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor =
        if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        // Campo visibile, non modificabile manualmente
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.currency), color = textColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedLabelColor = textColor,
                unfocusedLabelColor = textColor,
                focusedTrailingIconColor = textColor,
                unfocusedTrailingIconColor = textColor
            )
        )

        // Menu a tendina con tutte le valute disponibili
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(backgroundColor)
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency, color = textColor) },
                    onClick = {
                        onSelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Grafico a ciambella che rappresenta visivamente le proporzioni delle valute nel portafoglio.
 * Ogni segmento è colorato e rappresenta una valuta.
 */
@Composable
fun WalletDonutChart(
    entries: List<WalletEntry>,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.donutChartSize,
    thickness: Dp = Dimens.donutChartThickness
) {
    val total = entries.sumOf { it.amount }
    if (total == 0.0) return // Niente da mostrare se il portafoglio è vuoto

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(contentAlignment = Alignment.Center) {
        // Disegna la ciambella con segmenti colorati proporzionali agli importi
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

        // Mostra testo con valore e valuta selezionata
        selectedIndex?.let { i ->
            val entry = entries[i]
            Text(
                text = "${entry.currencyCode}: ${
                    String.format(
                        Locale.ITALY,
                        "%.2f",
                        entry.amount
                    )
                }",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    // Reset selezione quando cambia la lista
    LaunchedEffect(entries) {
        selectedIndex = null
    }
}