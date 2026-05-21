package com.turkcell.core.domain

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val imageUrl: String,
    val price: Double,
    val ticketTypes: List<TicketType> = emptyList()
)

data class TicketType(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val soldQuantity: Int = 0
) {
    val availableQuantity: Int
        get() = quantity - soldQuantity
}