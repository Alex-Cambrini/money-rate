package it.unibo.composeui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.unibo.composeui.R
import it.unibo.composeui.theme.DarkSurface
import it.unibo.composeui.theme.Dimens
import it.unibo.composeui.viewmodel.WalletViewModel
import it.unibo.domain.model.WalletEntry
import java.util.Locale

@Composable
fun WalletEntryCard(
    entry: WalletEntry,
    rates: Map<String, Double>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    viewModel: WalletViewModel
) {
    val euroValue = if (rates.isNotEmpty()) {
        viewModel.convertToEuro(entry, rates)
    } else 0.0
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) DarkSurface else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
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
                Text(entry.currencyCode, style = MaterialTheme.typography.bodyMedium)
                Text(entry.currencyName, style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    String.format(Locale.ITALY, "%.2f", entry.amount),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    String.format(Locale.ITALY, "%.2f€", euroValue),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    IconButton(onClick = { onEdit(entry.id) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_description)
                        )
                    }
                    IconButton(onClick = { onDelete(entry.id) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_description)
                        )
                    }
                }
            }
        }
    }
}
