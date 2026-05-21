package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String, // VALID, USED
    val ticketTypeId: String
)

// GET /me/tickets Response
// [
//   {
//     "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//     "qrCode": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//     "status": "VALID",
//     "ticketTypeId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
//   }
// ]