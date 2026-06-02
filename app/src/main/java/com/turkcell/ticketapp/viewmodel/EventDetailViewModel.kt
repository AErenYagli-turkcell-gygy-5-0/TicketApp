package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.data.network.ApiException
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val event: Event? = null,
    val selections: Map<String, Int> = emptyMap(),
    val isPurchasing: Boolean = false,
    val pendingPurchaseId: String? = null,
    val showConfirmDialog: Boolean = false,
    val purchaseError: String? = null,
    val paidSuccess: Boolean = false
) {
    val totalCents: Int
        get() {
            val e = event ?: return 0
            return e.ticketTypes.sumOf { type -> (selections[type.id] ?: 0) * type.priceCents }
        }
    val totalPrice: Double get() = totalCents / 100.0
    val hasSelection: Boolean get() = selections.values.any { it > 0 }
}

private const val MAX_PER_TYPE = 20

class EventDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailUiState())
    val state: StateFlow<EventDetailUiState> = _state.asStateFlow()

    fun load(eventId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            eventRepository.getEventDetail(eventId)
                .onSuccess { event -> _state.update { it.copy(isLoading = false, event = event) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }

    fun increment(ticketTypeId: String) {
        val event = _state.value.event ?: return
        val type = event.ticketTypes.find { it.id == ticketTypeId } ?: return
        val max = minOf(MAX_PER_TYPE, type.remaining)
        _state.update { state ->
            val current = state.selections[ticketTypeId] ?: 0
            if (current >= max) state
            else state.copy(selections = state.selections + (ticketTypeId to current + 1))
        }
    }

    fun decrement(ticketTypeId: String) {
        _state.update { state ->
            val current = state.selections[ticketTypeId] ?: 0
            if (current <= 0) state
            else state.copy(selections = state.selections + (ticketTypeId to current - 1))
        }
    }

    fun maxFor(ticketTypeId: String): Int {
        val type = _state.value.event?.ticketTypes?.find { it.id == ticketTypeId } ?: return 0
        return minOf(MAX_PER_TYPE, type.remaining)
    }

    fun startPurchase() {
        val current = _state.value
        val selected = current.selections.filterValues { it > 0 }
        if (selected.isEmpty() || current.isPurchasing) return

        _state.update { it.copy(isPurchasing = true, purchaseError = null) }
        viewModelScope.launch {
            eventRepository.createPurchase(selected)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(
                            isPurchasing = false,
                            pendingPurchaseId = purchase.id,
                            showConfirmDialog = true
                        )
                    }
                }
                .onFailure { error -> handlePurchaseError(error) }
        }
    }

    fun confirmPayment() {
        val purchaseId = _state.value.pendingPurchaseId ?: return
        _state.update { it.copy(isPurchasing = true, purchaseError = null) }
        viewModelScope.launch {
            eventRepository.pay(purchaseId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isPurchasing = false,
                            showConfirmDialog = false,
                            pendingPurchaseId = null,
                            paidSuccess = true
                        )
                    }
                }
                .onFailure { error -> handlePurchaseError(error) }
        }
    }

    fun dismissDialog() {
        _state.update { it.copy(showConfirmDialog = false, pendingPurchaseId = null, purchaseError = null) }
    }

    fun consumePurchaseError() = _state.update { it.copy(purchaseError = null) }

    private fun handlePurchaseError(error: Throwable) {
        val apiCode = (error as? ApiException)?.code
        val message = when (apiCode) {
            409 -> "Stok yetersiz, yenile"
            403 -> "Bu satın almanın sahibi değilsiniz"
            else -> error.toUserMessage()
        }
        _state.update {
            it.copy(isPurchasing = false, purchaseError = message)
        }
        if (apiCode == 409) {
            _state.value.event?.id?.let { reloadEvent(it) }
        }
    }

    private fun reloadEvent(eventId: String) {
        viewModelScope.launch {
            eventRepository.getEventDetail(eventId)
                .onSuccess { event -> _state.update { it.copy(event = event) } }
        }
    }
}