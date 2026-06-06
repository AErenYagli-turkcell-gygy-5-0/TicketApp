package com.turkcell.data.repository

import com.turkcell.core.domain.CheckinRepository
import com.turkcell.core.domain.CheckinResult
import com.turkcell.data.dto.ScanRequestDto
import com.turkcell.data.remote.CheckinApi
import com.turkcell.data.util.runCatchingApi

class CheckinRepositoryImpl(
    private val checkinApi: CheckinApi
) : CheckinRepository {

    override suspend fun scan(qrCode: String): Result<CheckinResult> = runCatchingApi {
        checkinApi.scan(ScanRequestDto(qrCode))
    }.map { dto ->
        CheckinResult(
            ticketId = dto.ticketId,
            ticketType = dto.ticketType,
            eventName = dto.event.name,
            place = dto.event.place,
            checkedInAt = dto.checkedInAt
        )
    }
}