package com.turkcell.core.domain

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val place: String,
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketType> = emptyList()
)

data class TicketType(
    val id: String,
    val name: String,
    val priceCents: Int,
    val capacity: Int,
    val soldCount: Int,
    val remaining: Int
) {
    val price: Double
        get() = priceCents / 100.0
}