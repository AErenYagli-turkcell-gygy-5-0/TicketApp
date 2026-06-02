package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.core.domain.TicketType
import com.turkcell.ticketapp.R
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onPaidSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(eventId) {
        viewModel.load(eventId)
    }

    // Ödeme başarılı → Biletlerim'e git
    LaunchedEffect(state.paidSuccess) {
        if (state.paidSuccess) onPaidSuccess()
    }

    // Ödeme onay dialog'u
    if (state.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { if (!state.isPurchasing) viewModel.dismissDialog() },
            title = { Text(stringResource(R.string.payment_dialog_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.payment_dialog_total, state.totalPrice))
                    if (state.purchaseError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = state.purchaseError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmPayment,
                    enabled = !state.isPurchasing
                ) {
                    if (state.isPurchasing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.payment_dialog_confirm))
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDialog,
                    enabled = !state.isPurchasing
                ) {
                    Text(stringResource(R.string.payment_dialog_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.event?.name ?: stringResource(R.string.event_detail_default_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (state.event != null) {
                Surface(tonalElevation = 3.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.event_detail_total, state.totalPrice),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.purchaseError != null && !state.showConfirmDialog) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = state.purchaseError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = viewModel::startPurchase,
                            enabled = state.hasSelection && !state.isPurchasing,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isPurchasing && !state.showConfirmDialog) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(stringResource(R.string.event_detail_buy))
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load(eventId) }) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }

                state.event != null -> {
                    val event = state.event!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(event.description, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(12.dp))
                            Text("📍 ${event.place}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("🗓 ${event.startsAt}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(20.dp))
                            Text(
                                stringResource(R.string.event_detail_ticket_types),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        items(event.ticketTypes, key = { it.id }) { type ->
                            TicketTypeRow(
                                type = type,
                                quantity = state.selections[type.id] ?: 0,
                                max = viewModel.maxFor(type.id),
                                onIncrement = { viewModel.increment(type.id) },
                                onDecrement = { viewModel.decrement(type.id) }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketTypeRow(
    type: TicketType,
    quantity: Int,
    max: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(type.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.event_detail_remaining, type.remaining, type.capacity),
                style = MaterialTheme.typography.bodySmall
            )
            Text("₺%.2f".format(type.price), style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(onClick = onDecrement, enabled = quantity > 0) {
                Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.event_detail_decrease))
            }
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
            FilledTonalIconButton(onClick = onIncrement, enabled = quantity < max) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.event_detail_increase))
            }
        }
    }
}