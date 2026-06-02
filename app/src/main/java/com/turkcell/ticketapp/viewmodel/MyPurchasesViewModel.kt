package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.Purchase
import com.turkcell.data.network.ApiException
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyPurchasesUiState(
    val purchases: List<Purchase> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val payingId: String? = null,      // o an pay edilen purchase
    val payError: String? = null,
    val paidSuccess: Boolean = false   // bir ödeme başarıyla tamamlandı → Biletlerim'e
) {
    val isEmpty: Boolean get() = !isLoading && error == null && purchases.isEmpty()
}

class MyPurchasesViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyPurchasesUiState())
    val state: StateFlow<MyPurchasesUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.update { it.copy(isLoading = true, error = null) }
        fetch()
    }

    fun refresh() {
        _state.update { it.copy(isRefreshing = true, error = null) }
        fetch()
    }

    private fun fetch() {
        viewModelScope.launch {
            eventRepository.getMyPurchases()
                .onSuccess { purchases ->
                    _state.update {
                        it.copy(
                            purchases = purchases,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.toUserMessage()
                        )
                    }
                }
        }
    }

    fun continuePayment(purchaseId: String) {
        if (_state.value.payingId != null) return
        _state.update { it.copy(payingId = purchaseId, payError = null) }
        viewModelScope.launch {
            eventRepository.pay(purchaseId)
                .onSuccess {
                    _state.update { it.copy(payingId = null, paidSuccess = true) }
                }
                .onFailure { error ->
                    val apiCode = (error as? ApiException)?.code
                    val message = when (apiCode) {
                        409 -> "Stok yetersiz ya da zaten ödenmiş, liste yenilendi"
                        403 -> "Bu satın almanın sahibi değilsiniz"
                        else -> error.toUserMessage()
                    }
                    _state.update { it.copy(payingId = null, payError = message) }
                    // 409 (capacity_exceeded / already_paid) → listeyi tazele
                    if (apiCode == 409) fetch()
                }
        }
    }

    fun consumePayError() = _state.update { it.copy(payError = null) }
}