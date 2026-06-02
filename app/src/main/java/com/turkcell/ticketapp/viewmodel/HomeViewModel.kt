package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.Event
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.AuthRepository
import com.turkcell.core.domain.Purchase
import com.turkcell.core.domain.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.turkcell.ticketapp.util.toUserMessage

data class EventsUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class TicketsUiState(
    val tickets: List<Ticket> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class PurchasesUiState(
    val purchases: List<Purchase> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HomeUiState(
    val eventsState: EventsUiState = EventsUiState(),
    val ticketsState: TicketsUiState = TicketsUiState(),
    val purchasesState: PurchasesUiState = PurchasesUiState(),
    val isRefreshing: Boolean = false
)

class HomeViewModel(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        loadEvents()
        loadTickets()
        loadPurchases()
    }

    fun refreshData() {
        _state.value = _state.value.copy(isRefreshing = true)
        loadEvents()
        loadTickets()
        loadPurchases()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun loadEvents() {
        _state.value = _state.value.copy(
            eventsState = _state.value.eventsState.copy(isLoading = true, error = null)
        )

        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { events ->
                    _state.value = _state.value.copy(
                        eventsState = EventsUiState(
                            events = events,
                            isLoading = false,
                            error = null
                        )
                    )
                    checkRefreshComplete()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        eventsState = EventsUiState(
                            events = _state.value.eventsState.events,
                            isLoading = false,
                            error = error.toUserMessage()
                        )
                    )
                    checkRefreshComplete()
                }
        }
    }

    private fun loadTickets() {
        _state.value = _state.value.copy(
            ticketsState = _state.value.ticketsState.copy(isLoading = true, error = null)
        )

        viewModelScope.launch {
            eventRepository.getMyTickets()
                .onSuccess { tickets ->
                    _state.value = _state.value.copy(
                        ticketsState = TicketsUiState(
                            tickets = tickets,
                            isLoading = false,
                            error = null
                        )
                    )
                    checkRefreshComplete()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        ticketsState = TicketsUiState(
                            tickets = _state.value.ticketsState.tickets,
                            isLoading = false,
                            error = error.toUserMessage()
                        )
                    )
                    checkRefreshComplete()
                }
        }
    }

    private fun loadPurchases() {
        _state.value = _state.value.copy(
            purchasesState = _state.value.purchasesState.copy(isLoading = true, error = null)
        )

        viewModelScope.launch {
            eventRepository.getMyPurchases()
                .onSuccess { purchases ->
                    _state.value = _state.value.copy(
                        purchasesState = PurchasesUiState(
                            purchases = purchases,
                            isLoading = false,
                            error = null
                        )
                    )
                    checkRefreshComplete()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        purchasesState = PurchasesUiState(
                            purchases = _state.value.purchasesState.purchases,
                            isLoading = false,
                            error = error.toUserMessage()
                        )
                    )
                    checkRefreshComplete()
                }
        }
    }

    private fun checkRefreshComplete() {
        val currentState = _state.value
        if (!currentState.eventsState.isLoading &&
            !currentState.ticketsState.isLoading &&
            !currentState.purchasesState.isLoading) {
            _state.value = currentState.copy(isRefreshing = false)
        }
    }
}

