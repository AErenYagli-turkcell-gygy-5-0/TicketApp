package com.turkcell.data.remote

import com.turkcell.data.dto.EventDto
import com.turkcell.data.dto.PurchaseDto
import com.turkcell.data.dto.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path

interface EventApi {
    // Etkinlikler
    @GET("/events")
    suspend fun getEvents(): List<EventDto>

    @GET("/events/{id}")
    suspend fun getEventDetail(@Path("id") eventId: String): EventDto

    // Biletlerim
    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>

    @GET("/me/tickets/{id}")
    suspend fun getTicketDetail(@Path("id") ticketId: String): TicketDto

    // Satın Almalarım
    @GET("/me/purchases")
    suspend fun getMyPurchases(): List<PurchaseDto>

    @GET("/me/purchases/{id}")
    suspend fun getPurchaseDetail(@Path("id") purchaseId: String): PurchaseDto
}