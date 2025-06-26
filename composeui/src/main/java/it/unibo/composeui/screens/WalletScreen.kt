package it.unibo.composeui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import it.unibo.composeui.theme.Background
import it.unibo.composeui.theme.DarkBackground
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.composeui.viewmodel.WalletViewModelFactory
import it.unibo.domain.model.WalletEntry
import it.unibo.domain.repository.CurrencyRateRepository
import it.unibo.domain.repository.CurrencyRepository
import it.unibo.domain.repository.WalletRepository
import it.unibo.domain.usecase.currency.GetAvailableCurrenciesUseCaseImpl
import it.unibo.domain.usecase.currencyrate.GetCachedRatesUseCaseImpl
import it.unibo.domain.usecase.currencyrate.RefreshCacheUseCaseImpl
import it.unibo.domain.usecase.wallet.AddEntryUseCaseImpl
import it.unibo.domain.usecase.wallet.GetAllEntriesUseCaseImpl
import it.unibo.domain.usecase.wallet.RemoveEntryUseCaseImpl
import it.unibo.domain.usecase.wallet.UpdateEntryUseCaseImpl
import it.unibo.composeui.resources.Strings

@Composable
fun WalletScreen(
    currencyRepository: CurrencyRepository,
    currencyRateRepository: CurrencyRateRepository,
    walletRepository: WalletRepository
) {
    val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(
            addEntryUseCase = AddEntryUseCaseImpl(walletRepository),
            updateEntryUseCase = UpdateEntryUseCaseImpl(walletRepository),
            removeEntryUseCase = RemoveEntryUseCaseImpl(walletRepository),
            getAllEntriesUseCase = GetAllEntriesUseCaseImpl(walletRepository),
            getAvailableCurrenciesUseCase = GetAvailableCurrenciesUseCaseImpl(currencyRepository),
            refreshCacheUseCase = RefreshCacheUseCaseImpl(currencyRateRepository),
            getCachedRatesUseCase = GetCachedRatesUseCaseImpl(currencyRateRepository)
        )
    )

    val combinedData by viewModel.combinedData.collectAsStateWithLifecycle()
    val entries = combinedData.entries
    val rates = combinedData.ratesCache

    val total by viewModel.total.collectAsState()
    val currencies: List<Pair<String, String>> by viewModel.currencies.collectAsStateWithLifecycle(emptyList())

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
                Icon(Icons.Default.Add, contentDescription = Strings.ADD_WALLET_DESCRIPTION)
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .padding(Dimens.elementSpacing)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
        ) {
            item {
                Text(
                    text = Strings.TOTAL_VALUE_FORMAT.format(total),
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
                        if (rates.isNotEmpty()) {
                            WalletDonutChart(
                                entries = entries,
                                modifier = Modifier.size(Dimens.donutChartSize),
                                size = Dimens.donutChartSize,
                                thickness = Dimens.donutChartThickness
                            )
                        } else {
                            CircularProgressIndicator(modifier = Modifier.size(Dimens.donutChartSize))
                        }


                        Column(
                            verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)
                        ) {
                            entries.forEachIndexed { index, item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(Dimens.legendBoxSize)
                                            .background(colors[index % colors.size], shape = CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(Dimens.elementSpacing))
                                    Text("${item.currencyCode}: %.2f".format(item.amount))
                                }
                            }
                        }
                    }
                }
            }

            item {
                if (entries.isEmpty()) {
                    Text(
                        text = Strings.NO_WALLETS,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Text(
                        text = Strings.WALLETS,
                        style = MaterialTheme.typography.titleLarge
                    )
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
                            .padding(Dimens.cardPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(item.currencyCode, style = MaterialTheme.typography.bodyMedium)
                            Text(item.currencyName, style = MaterialTheme.typography.bodySmall)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("%.2f".format(item.amount), style = MaterialTheme.typography.bodyMedium)
                            val euroValue = if (rates.isNotEmpty()) {
                                viewModel.convertToEuro(item, rates)
                            } else {
                                0.0
                            }
                            Text("%.2f€".format(euroValue), style = MaterialTheme.typography.bodySmall)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row {
                                IconButton(onClick = { editEntryId = item.id }) {
                                    Icon(Icons.Default.Edit, contentDescription = Strings.EDIT_DESCRIPTION)
                                }
                                IconButton(onClick = { deleteEntryId = item.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = Strings.DELETE_DESCRIPTION)
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
    availableCurrencies: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var selectedCurrency by remember { mutableStateOf(availableCurrencies.firstOrNull()?.first.orEmpty()) }
    var amountText by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor = if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.ADD_WALLET, color = textColor) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.elementSpacing)) {
                DropdownMenuCurrencySelector(
                    currencies = availableCurrencies.map { "${it.first} - ${it.second}" },
                    selected = availableCurrencies.find { it.first == selectedCurrency }?.let { "${it.first} - ${it.second}" } ?: "",
                    onSelected = { selectedString ->
                        selectedCurrency = selectedString.substringBefore(" - ")
                    }
                )
                TextField(
                    value = amountText,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() || ch == '.' }) amountText = it
                    },
                    label = { Text(Strings.AMOUNT, color = textColor) },
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
                val amount = amountText.toDoubleOrNull()
                if (amount != null && selectedCurrency.isNotBlank()) {
                    onConfirm(selectedCurrency, amount)
                }
            }) {
                Text(Strings.CONFIRM, color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.CANCEL, color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}

@Composable
fun EditWalletDialog(
    initialAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(initialAmount.toString()) }

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor = if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.EDIT_AMOUNT, color = textColor) },
        text = {
            TextField(
                value = amountText,
                onValueChange = {
                    if (it.all { ch -> ch.isDigit() || ch == '.' || ch == '-' }) amountText = it
                },
                label = { Text(Strings.DELTA, color = textColor) },
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
                Text(Strings.SAVE, color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.CANCEL, color = textColor)
            }
        },
        containerColor = backgroundColor
    )
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor = if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.DELETE_WALLET, color = textColor) },
        text = { Text(Strings.CONFIRM_DELETE_WALLET, color = textColor) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(Strings.DELETE_DESCRIPTION, color = textColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.CANCEL, color = textColor)
            }
        },
        containerColor = backgroundColor
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

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) DarkBackground else Background
    val textColor = if (isDark) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(Strings.CURRENCY, color = textColor) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
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

@Composable
fun WalletDonutChart(
    entries: List<WalletEntry>,
    modifier: Modifier = Modifier,
    size: Dp = Dimens.donutChartSize,
    thickness: Dp = Dimens.donutChartThickness
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

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(contentAlignment = Alignment.Center) {
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

        selectedIndex?.let { i ->
            val entry = entries[i]
            Text(
                text = "${entry.currencyCode}: ${"%.2f".format(entry.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
    LaunchedEffect(entries) {
        selectedIndex = null
    }
}