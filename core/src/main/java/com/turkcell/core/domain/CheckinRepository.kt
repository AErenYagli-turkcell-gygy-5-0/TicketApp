package com.turkcell.core.domain

interface CheckinRepository {
    suspend fun scan(qrCode: String): Result<CheckinResult>
}