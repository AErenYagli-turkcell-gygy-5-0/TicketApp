package com.turkcell.core.domain

data class CheckinResult(
    val ticketId: String,
    val ticketType: String,
    val eventName: String,
    val place: String,
    val checkedInAt: String
)