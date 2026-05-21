package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val imageUrl: String,
    val price: Double,
    val ticketTypes: List<TicketTypeDto> = emptyList()
)

@Serializable
data class TicketTypeDto(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val soldQuantity: Int = 0
)