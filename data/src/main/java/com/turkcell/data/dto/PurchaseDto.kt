package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: String,
    val status: String,
    val totalCents: Int,
    val paidAt: String? = null,
    val items: List<PurchaseItemDto> = emptyList(),
    val tickets: List<TicketDto> = emptyList()
)

@Serializable
data class PurchaseItemDto(
    val id: String,
    val ticketTypeId: String,
    val quantity: Int,
    val unitPriceCents: Int
)

