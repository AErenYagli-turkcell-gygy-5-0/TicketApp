package com.turkcell.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ScanRequestDto(
    val qrCode: String
)

@Serializable
data class ScanResultDto(
    val ticketId: String,
    val ticketType: String,
    val event: ScanEventDto,
    val checkedInAt: String
)

@Serializable
data class ScanEventDto(
    val id: String,
    val name: String,
    val place: String,
    val startsAt: String
)