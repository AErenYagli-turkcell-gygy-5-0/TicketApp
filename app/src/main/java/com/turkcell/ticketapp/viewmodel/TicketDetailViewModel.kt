package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.EventRepository
import com.turkcell.core.domain.Ticket
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketDetailUiState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TicketDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailUiState())
    val state: StateFlow<TicketDetailUiState> = _state.asStateFlow()

    fun load(ticketId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getTicketDetail(ticketId)
                .onSuccess { ticket -> _state.update { it.copy(isLoading = false, ticket = ticket) } }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUserMessage()) }
                }
        }
    }
}