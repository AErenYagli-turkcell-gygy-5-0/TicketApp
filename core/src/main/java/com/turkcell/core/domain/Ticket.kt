package com.turkcell.core.domain

enum class TicketStatus {
    VALID, USED, EXPIRED;

    companion object {
        fun fromString(value: String?): TicketStatus = when (value?.uppercase()) {
            "USED" -> USED
            "EXPIRED" -> EXPIRED
            else -> VALID
        }
    }
}

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: TicketStatus,
    val ticketTypeId: String
)

enum class PurchaseStatus {
    PENDING, COMPLETED, CANCELLED;

    companion object {
        fun fromString(value: String?): PurchaseStatus = when (value?.uppercase()) {
            "COMPLETED" -> COMPLETED
            "CANCELLED" -> CANCELLED
            else -> PENDING
        }
    }
}

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val totalCents: Int,
    val paidAt: String? = null,
    val items: List<PurchaseItem> = emptyList(),
    val tickets: List<Ticket> = emptyList()
) {
    val totalPrice: Double
        get() = totalCents / 100.0
}

data class PurchaseItem(
    val id: String,
    val ticketTypeId: String,
    val quantity: Int,
    val unitPriceCents: Int
) {
    val unitPrice: Double
        get() = unitPriceCents / 100.0
}