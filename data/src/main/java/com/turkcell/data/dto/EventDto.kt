package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val name: String,
    val description: String,
    val place: String,
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketTypeDto> = emptyList()
)

@Serializable
data class TicketTypeDto(
    val id: String,
    val name: String,
    val priceCents: Int,
    val capacity: Int,
    val soldCount: Int,
    val remaining: Int
)

